from django.conf.urls import patterns, url, include
from tastypie.api import Api

from payments.api import TransactionResource, AccountDetailsResource, MobileResource, UpiResource

payments_api = Api('payments_api')
payments_api.register(TransactionResource())
payments_api.register(AccountDetailsResource())
payments_api.register(MobileResource())
payments_api.register(UpiResource())

urlpatterns = patterns('',

                         url(r'^api/', include(payments_api.urls))

                       )