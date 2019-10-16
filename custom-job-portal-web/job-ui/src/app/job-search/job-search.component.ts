import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JobService } from '../service/job-service/job.service';
import { JobDto } from '../model/JobDto';

@Component({
  selector: 'app-job-search',
  templateUrl: './job-search.component.html',
  styleUrls: ['./job-search.component.css']
})
export class JobSearchComponent implements OnInit {

  jobDto: JobDto;
  errorHappened = false;

  constructor(private route: ActivatedRoute, private router: Router, private jobService: JobService) { }

  ngOnInit() {
    this.jobDto = new JobDto();
    this.jobDto.id = null;
    this.jobDto.normalizedTitle = null;
  }

  onSubmit() {
    this.jobService.searchJob(this.jobDto)
      .subscribe(
        savedJobDto => {
          this.errorHappened = false;
          this.router.navigate(['job', savedJobDto.id, 'positions']);
        },
        error => {
          console.log(error);
          this.errorHappened = true;
        });
  }

}
