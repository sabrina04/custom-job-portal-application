import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { JobSearchComponent } from './job-search/job-search.component';
import { JobListComponent } from './job-list/job-list.component';
import { JobPositionComponent } from './job-position/job-position.component';


const routes: Routes = [
  { path: 'job', component: JobSearchComponent },
  { path: 'jobs', component: JobListComponent },
  { path: 'job/:id/positions', component: JobPositionComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
