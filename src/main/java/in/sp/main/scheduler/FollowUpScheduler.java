package in.sp.main.scheduler;

import in.sp.main.service.LeadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FollowUpScheduler {

    @Autowired
    private LeadService leadService;

    // Runs every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?",
               zone = "Asia/Kolkata")
    public void checkFollowUps() {

        try {

            var today =
                    leadService.getTodayFollowUps();

            var overdue =
                    leadService.getOverdueFollowUps();

            System.out.println(
                    "\n========== FOLLOW-UP REMINDER ==========");

            System.out.println(
                    "Today's Follow-Ups : "
                    + today.size());

            System.out.println(
                    "Overdue Follow-Ups : "
                    + overdue.size());

            System.out.println(
                    "========================================\n");

        } catch(Exception e) {

            e.printStackTrace();
        }
    }
}