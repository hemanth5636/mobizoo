import random


from django.conf.urls import url
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
import requests
from tastypie import fields
from tastypie.authentication import Authentication
from tastypie.authorization import Authorization
from tastypie.constants import ALL, ALL_WITH_RELATIONS
from tastypie.exceptions import BadRequest
from tastypie.http import HttpUnauthorized, HttpForbidden
from tastypie.resources import ModelResource
from tastypie.utils import trailing_slash

from users.models import User, BankInfo, BankAccountDetails, UpiDetails


class UserResource(ModelResource):
    class Meta:
        queryset = User.objects.all()
        resource_name = 'user'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'email': ALL,
            'user_type': ALL,
            'first_name': ALL,
            'middle_name': ALL,
            'last_name': ALL,
            'mobile': ALL,
            'created_at': ALL,
        }

    def override_urls(self):
        return [
            url(r"^(?P<resource_name>%s)/login%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('login'), name="api_login"),
            url(r"^(?P<resource_name>%s)/is_logged_in%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('isLoggedIn'), name="api_isloggedin"),
            url(r"^(?P<resource_name>%s)/logout%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('logout'), name="api_logout"),
            url(r"^(?P<resource_name>%s)/resend_otp%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('resend_otp'), name="api_resend_otp"),
            url(r"^(?P<resource_name>%s)/request_otp%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('request_otp'), name="api_request_otp"),
            url(r"^(?P<resource_name>%s)/verify_otp%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('verify_otp'), name="api_verify_otp"),
            url(r"^(?P<resource_name>%s)/fetch_user_details%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('fetch_user_details'), name="api_fetch_user_details"),
            url(r"^(?P<resource_name>%s)/request_money%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('request_money'), name="api_request_money"),

    ]

    def fetch_user_details(self, request, **kwargs):
        if request.session.test_cookie_worked():
            print ("The test cookie worked!!!")
            request.session.delete_test_cookie()
       # print (request.user.first_name)
        if request.user.is_authenticated():
            user = User.objects.get(id=request.user.id)
            bundle = UserResource().build_bundle(obj=user, request=request)
            bundle = UserResource().full_dehydrate(bundle=bundle)
            return self.create_response(request, {"success": True,
                                                  "user": bundle})
        else:
            return self.create_response(request, {"success": False,
                                                  "details": "user not authenticated"})

    def sendSMS(self, numbers, sender, message):
        hashCode = "b54aae2ddf1e0baf696ac964e7e161931739c78ab8deab30736ab346c532a9ed"
        uname = "hemanth5636@gmail.com"
        #data = urllib.parse.urlencode({'username': uname, 'hash': hashCode, 'numbers': numbers,
         #                              'message': message, 'sender': sender})
        #data = data.encode('utf-8')
        #request = urllib.request.Request("http://api.textlocal.in/send/?")
        #f = urllib.request.urlopen(request, data)
        #fr = f.read()
        #return (fr)

    def verify_otp(self, request, **kwargs):
        self.method_check(request, allowed=['get'])
        if request.user.is_authenticated():
            user=None
            try:
                user = User.objects.get(user=request.user)
            except User.DoesNotExist:
                return self.create_response(request, {"success":False,
                                                      "details":"unauthorised user"})
            if user.otp == request.GET.get("otp"):
                user.otp_verified = True
                user.save()
                return self.create_response(request, {"success":True,
                                                      "user": UserResource().full_dehydrate(UserResource().build_bundle(obj=user))})
            else:
                return self.create_response(request, {"success":False,
                                                      "details":"Incorrect OTP"})

    def resend_otp(self, request, **kwargs):
        if request.user.is_authenticated():
            user=None
            user = User.objects.get(user=request.user)
            if user.mobile is None:
                return self.create_response(request, {"success": False,
                                                      "details": "please upload your mobile number"})
            user.otp = random.randint(100000, 999999)
            user.otp_verified = False
            user.save()
            post_data = {
                "api_key": "281ae9ab",
                "api_secret": "5a973c78f71b730a",
                "to": user.mobile,
                "from": "Mobizoo",
                "text": "Your OTP is "+user.otp
            }
            text = "Your OTP is "+str(user.otp)
            res = self.sendSMS(user.mobile, 'Jims Autos', text)
            print(res.data)
            #response_text = requests.post("https://rest.nexmo.com/sms/json", data=post_data)
            return self.create_response(request, {"success": True,
                                                  "details": "OTP sent successfully"})

    def request_otp(self, request, **kwargs):
        if request.user.is_authenticated():

            user = User.objects.get(email=request.user.email)
            if user.mobile is None:
                return self.create_response(request, {"success": False,
                                                      "details": "please upload your mobile number"})
            user.otp = random.randint(100000, 999999)
            user.otp_verified = False
            user.save()
            post_data = {
                "api_key": "281ae9ab",
                "api_secret": "5a973c78f71b730a",
                "to": "91"+str(user.mobile),
                "from": "Mobizoo",
                "text": "Your OTP is " + str(user.otp)

            }
            print(user.mobile)
            text = "Your OTP is " + str(user.otp)
            to = "91"+str(user.mobile)
            res = self.sendSMS(numbers=to, sender="hema", message=text)
            print(res.data)
            #response_text = requests.post("https://rest.nexmo.com/sms/json", data=post_data)
            #print(response_text)
            return self.create_response(request, {"success": True,
                                                  "details": "OTP sent successfully"})

    def request_money(self, request, **kwargs):
        if request.user.is_authenticated():
            data = self.deserialize(request, request.body, format=request.META.get('CONTENT_TYPE', 'application/json'))
            mobile_number = data.get("mobile")
            amount = data.get("amount")
            message = data.get("message")

            return self.create_response(request, {'success':True,
                                                  'details':"request sent succesflly"})
        else:
            return self.create_response(request, {'success': False,
                                                  "details": "user not authenticated please login",
                                                  "error_code": 111})

    def isLoggedIn(self, request, **kwargs):
        self.method_check(request, allowed=['get'])

        if request.user.is_authenticated():
            return self.create_response(request, {
                'success': True
            })
        else:
            return self.create_response(request, {
                'success': False
            }, HttpForbidden)

    def login(self, request, **kwargs):
        self.method_check(request, allowed=['post'])

        data = self.deserialize(request, request.body, format=request.META.get('CONTENT_TYPE', 'application/json'))
        email = data.get('email')
        new_user = False
        try:
            user = User.objects.get(email=email)
            new_user = False
        except User.DoesNotExist:
            u = User()
            u.email = data.get('email')
            u.first_name = data.get('first_name', "")
            u.last_name = data.get('last_name', "")
            u.profile_pic_url = data.get('profile_pic_url', "")
            u.details = data.get('details', "")
            u.user_type = data.get('user_type')
            u.save()
            new_user = True
        user = authenticate(email=email)
        if user and request.GET.get('type') == 'user':
            if user.user_type > 0:
                return self.create_response(request, {
                    'success': False,
                    'details': 'An vendor account is existed with the same email, try to login with another email'

                })
            login(request, user)
            bundle = self.build_bundle(obj=user, request=request)
            bundle = self.full_dehydrate(bundle)
            return self.create_response(request, {
                'success': True,
                'sessionKey': request.session.session_key,
                'user': bundle,
                'new_user': new_user
            })
        elif user and request.GET.get('type') == 'vendor':
            if user.user_type == 0:
                return self.create_response(request, {
                    'success': False,
                    'details': 'An User account is existed with the same email, try to login with another email'

                })
            login(request, user)
            bundle = self.build_bundle(obj=user, request=request)
            bundle = self.full_dehydrate(bundle)
            return self.create_response(request, {
                'success': True,
                'sessionKey': request.session.session_key,
                'user': bundle,
                'new_user': new_user
            })

        else:
            return self.create_response(request, {
                'success': False,
                'details': 'login failed'

            })

    def logout(self, request, **kwargs):
        self.method_check(request, allowed=['get'])
        if request.user and request.user.is_authenticated():
            logout(request)
            return self.create_response(request, {'success': True})
        else:
            return self.create_response(request, {'success': False}, HttpUnauthorized)


class BankInfoResource(ModelResource):
    class Meta:
        queryset = BankInfo.objects.all()
        resource_name = 'bank_info'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
        }


class BankAccountDetailsResource(ModelResource):
    user = fields.ForeignKey(UserResource, 'user', full=False)
    bank = fields.ForeignKey(BankInfoResource, 'bank', full=True)

    class Meta:
        queryset = BankAccountDetails.objects.all()
        resource_name = 'bank_details'
        list_allowed_methods = ['get', 'post', 'patch', 'put', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'user': ALL_WITH_RELATIONS,
            'bank': ALL_WITH_RELATIONS
        }

    def get_object_list(self, request):
        if request.user.is_authenticated():
            return super(BankAccountDetailsResource, self).get_object_list(request).filter(user=request.user)
        else:
            return None

    def override_urls(self):
        return [
            url(r"^(?P<resource_name>%s)/save_bank%s$" %
                (self._meta.resource_name, trailing_slash()),
                self.wrap_view('save_bank'), name="api_save_bank"),
        ]

    def save_bank(self, request, **kwargs):
        self.method_check(request, allowed=['post'])
        if request.user.is_authenticated():
            data = self.deserialize(request, request.body, format=request.META.get('CONTENT_TYPE', 'application/json'))
            bank = BankAccountDetails()
            bank.account_no = data.get('account_no')
            bank.holder_name = data.get('holder_name')
            bank.ifsc_code = data.get('ifsc_code')
            bank.account_state = 0
            bank.bank_id = 1
            bank.user = request.user
            bank.save()
            bundle = BankAccountDetailsResource().build_bundle(obj=bank)
            bundle = BankAccountDetailsResource().full_dehydrate(bundle=bundle)
            return self.create_response(request, {"success":True,
                                                  "details":"bank details added successfully",
                                                  "data":bundle
                                                  })
        else :
            return self.create_response(request, {"success":False,
                                                  "details":"user not authenticated, please login again"})


class UpiDetailsResource(ModelResource):
    user = fields.ForeignKey(UserResource, 'user', full=True)

    class Meta:
        queryset = UpiDetails.objects.all()
        resource_name = 'upi_details'
        list_allowed_methods = ['get', 'post', 'patch', 'delete']
        authorization = Authorization()
        include_resource_uri = True
        always_return_data = True
        authentication = Authentication()
        filtering = {
            'id': ALL,
            'user': ALL_WITH_RELATIONS,

        }


