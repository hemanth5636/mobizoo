from _ast import Store

from django.conf.urls import url
from tastypie import fields
from tastypie.authentication import Authentication
from tastypie.authorization import Authorization
from tastypie.constants import ALL, ALL_WITH_RELATIONS
from tastypie.resources import ModelResource
from tastypie.utils import trailing_slash

from users.api import UserResource
from vendors.models import CompanyCategory, Vendor, ContactInfo, Bill, BillingItems, Stores


class CompanyCategoryResource(ModelResource):

    class Meta:
        queryset = CompanyCategory.objects.all()
        resource_name = 'company_category'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'name': ALL,
            'details': ALL
        }


class VendorResource(ModelResource):
    user = fields.ForeignKey(UserResource, 'user', full=True,  null=True)
    company_category = fields.ForeignKey(CompanyCategoryResource, 'company_category', full=True, null=True)

    class Meta:
        queryset = Vendor.objects.all()
        resource_name = 'vendor'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'user': ALL_WITH_RELATIONS,
            'company_category': ALL_WITH_RELATIONS
        }


class StoreResource(ModelResource):
    vendor = fields.ForeignKey(VendorResource, 'vendor', full=True, null=True)
    created_by = fields.ForeignKey(UserResource, 'created_by', full=True, null=True)

    class Meta:
        queryset = Stores.objects.all()
        resource_name = 'stores'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'created_by': ALL_WITH_RELATIONS,
            'vendor': ALL_WITH_RELATIONS
        }


class ContactInfoResource(ModelResource):
    created_by = fields.ForeignKey(UserResource, 'user', full=True)

    class Meta:
        queryset = ContactInfo.objects.all()
        resource_name = 'contact_info'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'created_by': ALL_WITH_RELATIONS,

        }


class BillResource(ModelResource):
    sender = fields.ForeignKey(UserResource, 'sender', null=True, full=False)
    receiver = fields.ForeignKey(UserResource, 'receiver', null=True, full=True)
    store = fields.ForeignKey(StoreResource, 'store', null=True, full=True)

    class Meta:
        queryset = Bill.objects.all()
        resource_name = 'bill'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'sender': ALL_WITH_RELATIONS,
            'receiver':ALL_WITH_RELATIONS,
            'store':ALL_WITH_RELATIONS,
            'bill_type':ALL
        }

    def dehydrate(self, bundle):
        if bundle.obj.bill_type == 0:
            bill = BillingItemsResource()
            items = []
            obj = BillingItems.objects.filter(bill=bundle.obj)
            total_amount = 0
            total_items = 0
            for o in obj:
                bun = bill.build_bundle(obj=o)
                bun = bill.full_dehydrate(bundle=bun)
                items.append(bun)
            bundle.data['bill_items'] = items
            return bundle
        else:
            return bundle

    def override_urls(self):
        return [
            url(r"^(?P<resource_name>%s)/pay_bill%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('pay_bill'), name="api_pay_bill"),
        ]


class BillingItemsResource(ModelResource):
    bill = fields.ForeignKey(BillResource, 'bill', null=True, full=False)

    class Meta:
        queryset = BillingItems.objects.all()
        resource_name = 'bill_items'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'bill': ALL_WITH_RELATIONS,
            'product_name':ALL_WITH_RELATIONS,
            'time_stamp':ALL_WITH_RELATIONS,

        }