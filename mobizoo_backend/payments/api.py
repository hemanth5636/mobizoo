from django.conf.urls import url
from django.db.models import Q
from tastypie import fields
from tastypie.authentication import Authentication
from tastypie.authorization import Authorization
from tastypie.constants import ALL, ALL_WITH_RELATIONS
from tastypie.resources import ModelResource
from tastypie.utils import trailing_slash

from payments.models import Transaction, AccountDetails, Upi, Mobile
from users.api import UserResource, BankAccountDetailsResource, BankInfoResource
from users.models import BankAccountDetails, User
from vendors.api import BillResource, StoreResource
from vendors.models import CompanyCategory, Vendor, ContactInfo, Bill, BillingItems, Stores


class TransactionResource(ModelResource):
    bill = fields.ForeignKey(BillResource, 'bill', null=True, full=False)
    sender = fields.ForeignKey(UserResource, 'sender', null=True, full=True)
    receiver = fields.ForeignKey(UserResource, 'receiver', null=True, full=True)
    store = fields.ForeignKey(StoreResource, 'store',  null=True, full=True)
    class Meta:
        queryset = Transaction.objects.all()
        resource_name = 'transaction'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'bill': ALL_WITH_RELATIONS,
            'sender': ALL_WITH_RELATIONS,
            'receiver': ALL_WITH_RELATIONS
        }

    def get_object_list(self, request):
        return super(TransactionResource, self).get_object_list(request).filter(Q(sender=request.user)|Q(receiver=request.user)).order_by("-created_on")


    def override_urls(self):
        return [
            url(r"^(?P<resource_name>%s)/pay_bill%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('pay_bill'), name="api_pay_bill"),
            url(r"^(?P<resource_name>%s)/send_money%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('send_money'), name="api_send_money"),
        ]

    def pay_bill(self, request, **kwargs):
        if request.user.is_authenticated():
            data = self.deserialize(request, request.body, format=request.META.get('CONTENT_TYPE', 'application/json'))
            bill = Bill.objects.get(id=data.get('bill_id'))
            bill.sender = request.user
            bill.save()
            transcation = Transaction()
            transcation.sender = request.user
            transcation.receiver = bill.store.vendor.user
            transcation.transaction_type = 2
            transcation.amount = bill.total_amount
            transcation.bill = bill
            transcation.store = bill.store
            transcation.transaction_state = 1

            mobile = Mobile()
            mobile.transaction = transcation
            try:
                bank = BankAccountDetails.objects.get(user=request.user, account_state=1)
                mobile.sender_account = bank
            except BankAccountDetails.DoesNotExist:

                return self.create_response(request, {"success":False,
                                                      "status_code":101,
                                                      "details": "No active bank accounts or please link atleast one bank account"})
            transcation.save()
            bank = BankAccountDetails.objects.get(user=transcation.receiver, account_state=1)
            mobile.receiver_account = bank
            mobile.save()
            bundle = TransactionResource().build_bundle(obj=transcation)
            bundle = TransactionResource().full_dehydrate(bundle=bundle)
            return self.create_response(request, {'success': True,
                                                  "status_code": 100,
                                                  'transaction': bundle})

        else:
            return self.create_response(request, {'success':False,
                                                  "details":"user not authenticated please login",
                                                  "status_code": 111})

    def send_money(self, request, **kwargs):
        if request.user.is_authenticated():
            data = self.deserialize(request, request.body, format=request.META.get('CONTENT_TYPE', 'application/json'))
            if data.get('send_money_type') == 0:
                mobile_number = data.get('mobile')
                amount = data.get('amount')
                message = data.get('message')
                transaction = Transaction()
                transaction.sender = request.user
                try:
                    receiver = User.objects.get(mobile=mobile_number)
                except User.DoesNotExist:
                    return self.create_response(request, {"success": False,
                                                          "status_code": 102,
                                                          'details':"given mobile number is not associated with any account"})
                transaction.receiver = receiver
                transaction.amount = amount
                transaction.text = message
                transaction.transaction_type = 2
                transaction.transaction_state = 1
                transaction.save()
                mobile = Mobile()
                mobile.transaction = transaction
                try:
                    bank = BankAccountDetails.objects.get(user=request.user, account_state=1)
                    mobile.sender_account = bank
                    bank = BankAccountDetails.objects.get(user=transaction.receiver, account_state=1)
                    mobile.receiver_account = bank
                except BankAccountDetails.DoesNotExist:
                    return self.create_response(request, {"success": False,
                                                          "status_code": 101,
                                                          "details": "No active bank accounts or please link atleast one bank account"})

                mobile.save()
                bundle = TransactionResource().build_bundle(obj=transaction)
                bundle = TransactionResource().full_dehydrate(bundle=bundle)
                return self.create_response(request, {'success': True,
                                                      "status_code": 100,
                                                      'transaction': bundle})
            elif data.get('send_money_type') == 1:
                account_no = data.get('account_no')
                holder_name = data.get('holder_name')
                ifsc_code = data.get('ifsc_code')
                amount = data.get('amount')
                transaction = Transaction()
                transaction.sender = request.user
                transaction.amount = amount
                transaction.transaction_type = 0
                transaction.text = data.get('message')
                transaction.transaction_state = 1
                transaction.save()
                account_details = AccountDetails()
                account_details.transaction = transaction
                try:
                    account_details.sender_bank = BankAccountDetails.objects.get(user=request.user,account_state=BankAccountDetails.ACTIVE)
                except BankAccountDetails.DoesNotExist:
                    return self.create_response(request, {"success": False,
                                                          "status_code": 101,
                                                          "details": "No active bank accounts or please link atleast one bank account"})
                account_details.holder_name = holder_name
                account_details.account_no = account_no
                account_details.ifsc_code = ifsc_code
                account_details.save()

                account_details.save()
                bundle = TransactionResource().build_bundle(obj=transaction)
                bundle = TransactionResource().full_dehydrate(bundle=bundle)
                return self.create_response(request, {'success': True,
                                                      "status_code": 100,
                                                      'transaction': bundle})
            else:
                return self.create_response(request, {'success': False,
                                                      "details": "service not found",
                                                      "error_code": 404})
        else:
            return self.create_response(request, {'success': False,
                                                  "details": "user not authenticated please login",
                                                  "error_code": 111})


class AccountDetailsResource(ModelResource):
    sender_bank = fields.ForeignKey(BankAccountDetailsResource,'sender_bank', null=True, full=True)
    transaction = fields.ForeignKey(TransactionResource, 'transaction', null=True, full=False)
    bank = fields.ForeignKey(BankInfoResource, 'bank', null=True, full=False)

    class Meta:
        queryset = AccountDetails.objects.all()
        resource_name = 'account_details'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'transaction': ALL_WITH_RELATIONS,
            'sender_bank': ALL_WITH_RELATIONS,
            'bank': ALL_WITH_RELATIONS,

        }


class UpiResource(ModelResource):
    transaction = fields.ForeignKey(TransactionResource, 'transaction', null=True, full=False)

    class Meta:
        queryset = Upi.objects.all()
        resource_name = 'upi'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'transaction': ALL_WITH_RELATIONS,

        }


class MobileResource(ModelResource):
    transaction = fields.ForeignKey(TransactionResource, 'transaction', null=True, full=False)
    sender_account = fields.ForeignKey(BankAccountDetails, 'sender_bank', null=True, full=True)
    receiver_account = fields.ForeignKey(BankAccountDetails, 'receiver_account', null=True, full=True)

    class Meta:
        queryset = Mobile.objects.all()
        resource_name = 'mobile'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'transaction': ALL_WITH_RELATIONS,
            'sender_account': ALL_WITH_RELATIONS,
            'receiver_account': ALL_WITH_RELATIONS
        }

