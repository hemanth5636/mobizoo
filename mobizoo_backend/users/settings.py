from django.conf import settings
from django.contrib.auth.hashers import check_password
from .models import User

class SettingBackend(object):

    def authenticate(self, email=None):
        if email:
            try:
                user = User.objects.get(email=email)
                print user.first_name
            except User.DoesNotExist:
                print 'not authenticated'
                return None
            return user
        else:
            print 'no email'
            return None

    def get_user(self, id_):
        try:
            return User.objects.get(id=id_)
        except User.DoesNotExist:
            return None