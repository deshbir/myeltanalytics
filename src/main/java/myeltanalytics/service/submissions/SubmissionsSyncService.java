package myeltanalytics.service.submissions;

import myeltanalytics.model.JobInfo;

import org.springframework.stereotype.Service;

@Service(value="submissionsSyncService")
public class SubmissionsSyncService
{
    public JobInfo jobInfo = new JobInfo();    
   
}
