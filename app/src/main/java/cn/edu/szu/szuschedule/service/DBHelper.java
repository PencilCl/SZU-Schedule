package cn.edu.szu.szuschedule.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chenlin on 26/06/2017.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String studentTable = "CREATE TABLE `student` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`name` varchar(50) NOT NULL," +
                "`sex` char(2) NOT NULL," +
                "`stuNum` varchar(50) NOT NULL," +
                "`account` varchar(255) NOT NULL," +
                "`password` varchar(255) NOT NULL)";

        String lessonTable = "CREATE TABLE `lesson` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`lessonName` varchar(255) NOT NULL," +
                "`location` varchar(255) NOT NULL," +
                "`day` int(11) NOT NULL," +
                "`begin` int(11) NOT NULL," +
                "`end` int(11) NOT NULL)";

        String libraryTable = "CREATE TABLE `library` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`studentID` int(25) NOT NULL," +
                "`bookName` varchar(255) DEFAULT NULL," +
                "`startDate` datetime DEFAULT NULL," +
                "`endDate` datetime DEFAULT NULL," +
                "FOREIGN KEY (`studentID`) REFERENCES `student` (`id`))";

        String subjectTable = "CREATE TABLE `subject` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`subjectName` varchar(255) NOT NULL," +
                "`courseNum` varchar(255) NOT NULL"  +
                "`termNum` varchar(255) NOT NULL"  +
                "`homeworkID` int(11) DEFAULT NULL," +
                "FOREIGN KEY (`homeworkID`) REFERENCES `subjecthomework` (`id`))";

        String homeworkTable = "CREATE TABLE `homework` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`homeworkName` varchar(255) NOT NULL," +
                "`discription` varchar(255) NOT NULL," +
                "`subjectID` int(11) DEFAULT NULL," +
                "`score` int(11) DEFAULT NULL," +
                "`deadline` varchar(255) NOT NULL," +
                "FOREIGN KEY (`subjectID`) REFERENCES `subject` (`id`))";

        String attachmentTable = "CREATE TABLE `attachment` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`attachmentName` varchar(255) NOT NULL," +
                "`attachmentUrl` varchar(255) NOT NULL)";

        String blackboardTable = "CREATE TABLE `blackboard` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`subjectID` int(11) NOT NULL," +
                "`studentID` int(11) NOT NULL," +
                "FOREIGN KEY (`studentID`) REFERENCES `student` (`id`)," +
                "FOREIGN KEY (`subjectID`) REFERENCES `subject` (`id`))";

        String subjecthomeworkTable = "CREATE TABLE `subjecthomework` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`homeworkID` int(11) NOT NULL," +
                "`attachmentID` int(11) DEFAULT NULL," +
                "FOREIGN KEY (`attachmentID`) REFERENCES `attachment` (`id`)," +
                "FOREIGN KEY (`homeworkID`) REFERENCES `homework` (`id`))";

        String scheduleTable = "CREATE TABLE `schedule` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`studentID` int(11) NOT NULL," +
                "`lessonID` int(11) NOT NULL," +
                "FOREIGN KEY (`lessonID`) REFERENCES `lesson` (`id`)," +
                "FOREIGN KEY (`studentID`) REFERENCES `student` (`id`))";

        db.execSQL(studentTable);
        db.execSQL(lessonTable);
        db.execSQL(libraryTable);
        db.execSQL(subjectTable);
        db.execSQL(homeworkTable);
        db.execSQL(attachmentTable);
        db.execSQL(blackboardTable);
        db.execSQL(subjecthomeworkTable);
        db.execSQL(scheduleTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static SQLiteDatabase getDB(Context context) {
        return new DBHelper(context, "szu-scheduler", null, 2).getReadableDatabase();
    }
}
