import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgxPaginationModule } from 'ngx-pagination';
import { AppComponent } from './app.component';
import { JobSearchComponent } from './job-search/job-search.component';
import { JobListComponent } from './job-list/job-list.component';
import { JobPositionComponent } from './job-position/job-position.component';
import { JobService } from './service/job-service/job.service';

@NgModule({
  declarations: [
    AppComponent,
    JobSearchComponent,
    JobListComponent,
    JobPositionComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    NgxPaginationModule
  ],
  providers: [JobService],
  bootstrap: [AppComponent]
})
export class AppModule { }
