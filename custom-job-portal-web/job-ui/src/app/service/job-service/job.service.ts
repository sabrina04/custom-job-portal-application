import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { JobDto } from '../../model/JobDto';
import { JobPositionDto } from '../../model/JobPositionDto';
import { Observable } from 'rxjs';

@Injectable()
export class JobService {

    private baseUrl: string;
    private httpHeader = {
        headers: new HttpHeaders({'Content-Type': 'application/json'})
    };

    constructor(private http: HttpClient) {
        this.baseUrl = 'http://localhost:8080';
    }

    public searchJob(jobDto: JobDto): Observable<JobDto> {
        const url = `${this.baseUrl}/job`;
        return this.http.post<JobDto>(url, jobDto, this.httpHeader);
    }

    public getJobById(id: string): Observable<JobDto> {
        const url = `${this.baseUrl}/job/${id}`;
        return this.http.get<JobDto>(url);
    }

    public getAllJobs(): Observable<JobDto[]> {
        const url = `${this.baseUrl}/jobs`;
        return this.http.get<JobDto[]>(url);
    }

    public getJobPostionsById(id: string): Observable<JobPositionDto[]> {
        const url = `${this.baseUrl}/job/${id}/positions`;
        return this.http.get<JobPositionDto[]>(url);
    }
}
