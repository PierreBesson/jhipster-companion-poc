import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { JhiDateUtils } from 'ng-jhipster';
import { NgxWebstorageModule } from 'ngx-webstorage';

import { SERVER_API_URL } from 'app/app.constants';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';

function accountWithAuthorities(authorities: string[]): Account {
  return {
    activated: true,
    authorities,
    email: '',
    firstName: '',
    langKey: '',
    lastName: '',
    login: '',
    imageUrl: ''
  };
}

describe('Service Tests', () => {
  describe('Account Service', () => {
    let service: AccountService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, NgxWebstorageModule.forRoot()],
        providers: [JhiDateUtils]
      });

      service = TestBed.get(AccountService);
      httpMock = TestBed.get(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    describe('Service methods', () => {
      it('should call /account if user is undefined', () => {
        service.identity().subscribe();
        const req = httpMock.expectOne({ method: 'GET' });
        const resourceUrl = SERVER_API_URL + 'api/account';

        expect(req.request.url).toEqual(`${resourceUrl}`);
      });

      it('should call /account only once if not logged out after first authentication and should call /account again if user has logged out', () => {
        // Given the user is authenticated
        service.identity().subscribe();
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush({
          firstName: 'Unimportant'
        });

        // When I call
        service.identity().subscribe();

        // Then there is no second request
        httpMock.expectNone({ method: 'GET' });

        // When I log out
        service.authenticate(null);
        // and then call
        service.identity().subscribe();

        // Then there is a new request
        httpMock.expectOne({ method: 'GET' });
      });

      describe('hasAnyAuthority string parameter', () => {
        it('should return false if user is not logged', () => {
          const hasAuthority = service.hasAnyAuthority('ROLE_USER');
          expect(hasAuthority).toBe(false);
        });

        it('should return false if user is logged and has not authority', () => {
          service.authenticate(accountWithAuthorities(['ROLE_USER']));

          const hasAuthority = service.hasAnyAuthority('ROLE_ADMIN');

          expect(hasAuthority).toBe(false);
        });

        it('should return true if user is logged and has authority', () => {
          service.authenticate(accountWithAuthorities(['ROLE_USER']));

          const hasAuthority = service.hasAnyAuthority('ROLE_USER');

          expect(hasAuthority).toBe(true);
        });
      });

      describe('hasAnyAuthority array parameter', () => {
        it('should return false if user is not logged', () => {
          const hasAuthority = service.hasAnyAuthority(['ROLE_USER']);
          expect(hasAuthority).toBeFalsy();
        });

        it('should return false if user is logged and has not authority', () => {
          service.authenticate(accountWithAuthorities(['ROLE_USER']));

          const hasAuthority = service.hasAnyAuthority(['ROLE_ADMIN']);

          expect(hasAuthority).toBe(false);
        });

        it('should return true if user is logged and has authority', () => {
          service.authenticate(accountWithAuthorities(['ROLE_USER']));

          const hasAuthority = service.hasAnyAuthority(['ROLE_USER', 'ROLE_ADMIN']);

          expect(hasAuthority).toBe(true);
        });
      });
    });
  });
});
