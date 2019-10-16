import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JobService } from '../service/job-service/job.service';
import { JobDto } from '../model/JobDto';

@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.css']
})
export class JobListComponent implements OnInit {

  jobs: JobDto[];
  dataLoadingDone = false;

  constructor(private route: ActivatedRoute, private router: Router, private jobService: JobService) { }

  ngOnInit() {
    this.jobService.getAllJobs().subscribe(
      jobList => {
        this.jobs = jobList;
        this.dataLoadingDone = true;
      },
      error => {
        this.dataLoadingDone = true;
        console.log(error);
      }
    );
  }

  onClick(job: JobDto) {
    this.router.navigate(['job', job.id, 'positions']);
  }

}
