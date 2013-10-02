/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evi.web.resources.cronreports;

import com.evi.web.resources.MailHandler;
import com.evi.web.resources.datamodelobject.compliance._CmpPrgRequiredReport;
import com.evi.web.resources.datamodelobject.employee._EmployeeAssignment;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author tao
 */
public class UIComplianceJob implements Job {

    private static Logger logger = Logger.getLogger(UIComplianceJob.class);

    public UIComplianceJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //Get today
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastEmailTime = context.getPreviousFireTime();
        
//        logger.fatal(">>>>SYNC@@@@@@@  >  # PRE:" + context.getPreviousFireTime());
//        logger.fatal(">>>>SYNC@@@@@@@  >  # NOW:" + context.getFireTime());
//        logger.fatal(">>>>SYNC@@@@@@@  >  # NEXT:" + context.getNextFireTime());

        if (lastEmailTime == null) {
            lastEmailTime = getPreviousTime();
//            logger.fatal(">>>>SYNC@@@@@@@  >  # IF NULL CALC AND SET TO:" + lastEmailTime);
        }

//                logger.fatal(">>>>SYNC@@@@@@@  >  # :" + cur.toString());
        Collection<_EmployeeAssignment> toBeAdded = new ArrayList<_EmployeeAssignment>();
        Collection<_EmployeeAssignment> collection = new ArrayList<_EmployeeAssignment>();
        StringBuilder date = new StringBuilder(dateFormat.format(lastEmailTime));
        //This job is specific to UI, so we use its resource.
        int resourceID = 3;

        try {
            logger.fatal("requesting EmpAssignments from " + date.toString());
            toBeAdded = _EmployeeAssignment.loadByAfterDate(date.toString());
//            logger.fatal(">>>>SYNC@@@@@@@  >  # TOTAL NUMBER OF ASSIGNMENT :" + toBeAdded.size() + "  AFTER DATE : " + date);
            for (_EmployeeAssignment itr : toBeAdded) {
                for (_CmpPrgRequiredReport report : itr.getComplianceProgram().getRequiredReports()) {
                    if (report.getDsID().compareToIgnoreCase("DRG_RPT") == 0
                            && itr.getComplianceProgram().getOwningResource().getResourceID() == resourceID) {
                        collection.add(itr);
                    }
                }
            }
        } catch (SQLException ex) {
            logger.fatal(">>>>SYNC@@@@@@@  >  # : SQL ERROR, E-MAIL NOT SENT.");
        }
        logger.fatal("ReportDrugUpload for " + collection.size() + " employees");
        MailHandler.reportDrugUpload(collection);
    }

    private Date getPreviousTime() {
        Calendar currentCal = new GregorianCalendar();

//        currentCal.set(Calendar.HOUR_OF_DAY, 17);
//        currentCal.set(Calendar.MINUTE, 31);

        currentCal.add(Calendar.MINUTE, -1);    // for safety

        Calendar previousCal1 = new GregorianCalendar();
        previousCal1.set(Calendar.HOUR_OF_DAY, 12);
        previousCal1.set(Calendar.MINUTE, 30);

        Calendar previousCal2 = new GregorianCalendar();
        previousCal2.set(Calendar.HOUR_OF_DAY, 9);
        previousCal2.set(Calendar.MINUTE, 30);

        Calendar previousCal3 = new GregorianCalendar();
        previousCal3.add(Calendar.DATE, -1);
        previousCal3.set(Calendar.HOUR_OF_DAY, 17);
        previousCal3.set(Calendar.MINUTE, 30);
        Date previousDate = null;

        if (currentCal.after(previousCal1)) {
            previousDate = previousCal1.getTime();
        } else if (currentCal.after(previousCal2)) {
            previousDate = previousCal2.getTime();
        } else if (currentCal.after(previousCal3)) {
            previousDate = previousCal3.getTime();
        }

        return previousDate;
    }
}
