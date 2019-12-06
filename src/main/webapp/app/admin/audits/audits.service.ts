import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { createRequestOption } from 'app/shared/util/request-util';
import { SERVER_API_URL } from 'app/app.constants';
import { Audit } from './audit.model';

@Injectable({ providedIn: 'root' })
export class AuditsService {
  constructor(private http: HttpClient) {}

  query(req: { page?: number; size?: number; sort?: string[]; fromDate: string; toDate: string }): Observable<HttpResponse<Audit[]>> {
    const params: HttpParams = createRequestOption(req);

    const requestURL = SERVER_API_URL + 'management/audits';

    return this.http.get<Audit[]>(requestURL, {
      params,
      observe: 'response'
    });
  }
}