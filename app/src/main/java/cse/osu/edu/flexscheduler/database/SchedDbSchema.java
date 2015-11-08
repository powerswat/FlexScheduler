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
}
