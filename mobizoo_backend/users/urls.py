from django.conf.urls import patterns, url, include
from tastypie.api import Api

from users.api import UserResource, BankAccountDetailsResource, BankInfoResource, UpiDetailsResource

user_api = Api('user_api')
user_api.register(UserResource())
user_api.register(BankAccountDetailsResource())
user_api.register(BankInfoResource())
user_api.register(UpiDetailsResource())


urlpatterns = patterns('',

                         url(r'^api/', include(user_api.urls))

                       )