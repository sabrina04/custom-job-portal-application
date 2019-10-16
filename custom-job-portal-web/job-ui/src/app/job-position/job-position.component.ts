import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JobService } from '../service/job-service/job.service';
import { JobPositionDto } from '../model/JobPositionDto';
import { JobDto } from '../model/JobDto';

@Component({
  selector: 'app-job-position',
  templateUrl: './job-position.component.html',
  styleUrls: ['./job-position.component.css']
})
export class JobPositionComponent implements OnInit {

  id: string;
  job: JobDto;
  jobPositions: JobPositionDto[];
  dataLoadingDone = false;
  jobFound = false;
  config: any;

  constructor(private route: ActivatedRoute, private router: Router, private jobService: JobService) {  }

  ngOnInit() {
    this.id = this.route.snapshot.params['id'];
    this.jobService.getJobById(this.id)
      .subscribe(
        jobDto => {
          this.job = jobDto;
        },
        error => {
          console.log(error);
        }
      );
    this.jobService.getJobPostionsById(this.id)
      .subscribe(
        jobPositionList => {
          this.jobPositions = jobPositionList;
          this.dataLoadingDone = true;
          if (this.jobPositions.length > 0) {
            this.jobFound = true;
            this.config = {
                itemsPerPage: 2,
                currentPage: 1,
                totalItems: this.jobPositions.length
            };
          }
        },
        error => {
          this.dataLoadingDone = true;
          console.log(error);
        });
  }

  pageChanged(event){
    this.config.currentPage = event;
  }

}
