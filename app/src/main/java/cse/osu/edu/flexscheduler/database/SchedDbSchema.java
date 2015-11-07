package cse.osu.edu.flexscheduler.database;

public class SchedDbSchema {
    public static final class SchedTable {
        public static final String NAME = "scheds1";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }

    public static final class UserTable {
        public static final String NAME = "user";

        public static final class Cols {
            public static final String EID = "eid";
            public static final String PID = "pid";
            public static final String EMAIL = "email";
            public static final String PASSWORD = "password";
            public static final String CURR_LOC = "curr_loc";
        }
    }
}
