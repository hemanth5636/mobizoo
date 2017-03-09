from _ast import Store

from tastypie import fields
from tastypie.authentication import Authentication
from tastypie.authorization import Authorization
from tastypie.constants import ALL, ALL_WITH_RELATIONS
from tastypie.resources import ModelResource

from users.api import UserResource
from vendors.models import CompanyCategory, Vendor, ContactInfo


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
    user = fields.ForeignKey(UserResource, full=True)
    company_category = fields.ForeignKey(CompanyCategoryResource, full=True)

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
    vendor = fields.ForeignKey(VendorResource, full=True)
    created_by = fields.ForeignKey(UserResource, full=True)

    class Meta:
        queryset = Store.objects.all()
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
    created_by = fields.ForeignKey(UserResource, full=True)

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


