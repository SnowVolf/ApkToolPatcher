package apk.tool.patcher.util;

public class Cs {
    public static final String ARG_PATH_NAME = "project_src_dir";
    public static final String ARG_BASE_PARAM = "base_parameter";
    public static final String ARG_HAS_BASE_PARAM = "has_base_parameter";
    public static final String ARG_PREF_TAB = "pref_startup_tab";
    public static final String ARG_ACTIONS = "executed_actions";
    public static final String ARG_APP_INFO = "app_info";

    public static final String TAB_MAIN = "0";
    public static final String TAB_DECOMPILER = "1";
    public static final String TAB_ABOUT = "2";

    public static final int DIALOGEVENT_CANCEL = -100;
    public static final int DIALOGID_CONFIRMRENAME = 5;
    public static final int DIALOGID_DELETEFILES = 2;
    public static final int DIALOGID_FINISH = 6;
    public static final int DIALOGID_INFO = 4;
    public static final int DIALOGID_SHOWTIME = 3;
    public static final int DIALOGID_TESTSUCCESS = 1;

    public static final int DLGFRAGMENT_BTN_NOYES = 1;
    public static final int DLGFRAGMENT_BTN_OK = 0;

    public static final String DLGFRAGMENT_BTN_TYPE = "dfbtntype";
    public static final String DLGFRAGMENT_DIALOG_ID = "dfdialogid";
    public static final String DLGFRAGMENT_MESSAGE_STR = "dfmsgstr";
    public static final String DLGFRAGMENT_TITLE_ID = "dftitleid";
    public static final String DLGFRAGMENT_WARNING = "dfwarn";

    public static final String EXTRA_APPEND_NAME = "appendname";
    public static final String EXTRA_ARCCOMMENT = "arccmt";
    public static final String EXTRA_ARCDIR = "arcdir";
    public static final String EXTRA_ARCINFO = "arcinfo";
    public static final String EXTRA_ARCNAME = "arcname";
    public static final String EXTRA_ARCNAME_MASK = "anamemask";
    public static final String EXTRA_ARC_FORMAT = "arcfmt";
    public static final String EXTRA_ARC_TO_SUBFOLDERS = "arctosubf";
    public static final String EXTRA_BROWSE_FILTER = "browseflt";
    public static final String EXTRA_BROWSE_FOLDERS_ONLY = "browseflonly";
    public static final String EXTRA_BROWSE_RESULT = "browseres";
    public static final String EXTRA_BROWSE_SAVE_FILE = "browsesavefile";
    public static final String EXTRA_BROWSE_SOURCE = "browsesrc";
    public static final String EXTRA_BROWSE_TITLE = "browsetitle";
    public static final String EXTRA_CMD_DATA = "cmddata";
    public static final String EXTRA_CMD_OPTYPE = "cmdoptype";
    public static final String EXTRA_COMP_METHOD = "compmethod";
    public static final String EXTRA_CUR_DIR = "curdir";
    public static final String EXTRA_DEL_FILES = "delfiles";
    public static final String EXTRA_DEST_PATH = "destpath";
    public static final String EXTRA_DICT_SIZE = "dictsize";
    public static final String EXTRA_DIR = "dir";
    public static final String EXTRA_DISPLAY_EXTRACTED = "dispextr";
    public static final String EXTRA_ENC_NAMES = "encnames";
    public static final String EXTRA_FAV_LOCATION = "favloc";
    public static final String EXTRA_FAV_NAME = "favname";
    public static final String EXTRA_FAV_POSITION = "favpos";
    public static final String EXTRA_FAV_REPLACE = "favreplace";
    public static final String EXTRA_FILE_MTIME = "fmtime";
    public static final String EXTRA_FILE_NAME = "fname";
    public static final String EXTRA_FILE_NORENAME = "fnoren";
    public static final String EXTRA_FILE_SIZE = "fsize";
    public static final String EXTRA_FOLDER_NAME = "foldername";
    public static final String EXTRA_GEN_ARCNAME = "genaname";
    public static final String EXTRA_GOTO_NAME = "gotoname";
    public static final String EXTRA_HELP_TOPIC = "helptopic";
    public static final String EXTRA_INT_LIST = "intlist";
    public static final String EXTRA_KEEP_BROKEN = "keepbrk";
    public static final String EXTRA_MASK_SELECT = "maskselect";
    public static final String EXTRA_NOPATH = "nopath";
    public static final String EXTRA_NOTIFY_KEY = "notifykey";
    public static final String EXTRA_NOTIFY_MESSAGE = "notifymsg";
    public static final String EXTRA_NOTIFY_TITLE = "notifytitle";
    public static final String EXTRA_OVERWRITE_MODE = "ovrmode";
    public static final String EXTRA_POPUP_TITLE = "popuptitle";
    public static final String EXTRA_PROFILE_DEFAULT = "profdef";
    public static final String EXTRA_PROFILE_NAME = "profname";
    public static final String EXTRA_PSW_TYPE = "pswtype";
    public static final String EXTRA_RAR4 = "rar4";
    public static final String EXTRA_RARX_CODE = "rarx";
    public static final String EXTRA_RECOVERY_SIZE = "rrsize";
    public static final String EXTRA_RECVOL_NUM = "recvolnum";
    public static final String EXTRA_RESULT_INT = "resint";
    public static final String EXTRA_RESULT_PSW = "respsw";
    public static final String EXTRA_RESULT_PSW_REMEMBER = "resrempsw";
    public static final String EXTRA_RESULT_STR = "resstr";
    public static final String EXTRA_SEL_NAMES = "selnames";
    public static final String EXTRA_SEPARATE_ARC = "separc";
    public static final String EXTRA_SHOW_TIME = "showtime";
    public static final String EXTRA_SINGLE_ARC = "singlearc";
    public static final String EXTRA_SOLID = "solid";
    public static final String EXTRA_STRING_LIST = "strlist";
    public static final String EXTRA_TEST_ARCHIVED = "testarchived";
    public static final String EXTRA_USE_BLAKE2 = "useblake2";
    public static final String EXTRA_VOL_PAUSE = "volpause";
    public static final String EXTRA_VOL_SIZE = "volsize";

    public static final int FMT_RAR = 0;
    public static final int FMT_ZIP = 1;

    public static final long INT64NDF = 0x7fffffff7fffffffL;

    public static final int OPTYPE_ADD = 1;
    public static final int OPTYPE_BENCHMARK = 7;
    public static final int OPTYPE_COPY = 8;
    public static final int OPTYPE_DELETE = 4;
    public static final int OPTYPE_EXTRACT = 2;
    public static final int OPTYPE_GETCOMMENT = 10;
    public static final int OPTYPE_LIST = 5;
    public static final int OPTYPE_NONE = 0;
    public static final int OPTYPE_RENAME = 9;
    public static final int OPTYPE_REPAIR = 6;
    public static final int OPTYPE_TEST = 3;

    public static final int OVRMODE_ALL = 89;
    public static final int OVRMODE_ASK = 63;
    public static final int OVRMODE_NONE = 78;

    public static final String PREFS_ARCFIRST = "prefs_arcfirst";
    public static final String PREFS_ARCHISTORY = "prefs_archistory";
    public static final String PREFS_ARCHISTORY_LIST = "ArcHistory";
    public static final String PREFS_ARC_CHARSET = "prefs_arc_charset";
    public static final String PREFS_DEF_EXTR_FOLDER = "prefs_def_extr_folder";
    public static final String PREFS_DEF_EXTR_MODE = "prefs_def_extr_mode";
    public static final String PREFS_EXTRPATH_APPEND = "prefs_extr_folder_append_name";
    public static final String PREFS_EXTRPATH_HISTORY = "prefs_extr_folder_history";
    public static final String PREFS_EXTRPATH_HISTORY_LIST = "ExtrPathHistory";
    public static final String PREFS_HIDDEN_FILES = "prefs_hidden_files";
    public static final String PREFS_SORT = "prefs_sort";
    public static final String PREFS_SOUND = "prefs_sound";
    public static final String PREFS_START_FOLDER = "prefs_start_folder";
    public static final String PREFS_THEME = "prefs_theme";
    public static final String PREFS_THUMBNAILS = "prefs_thumbnails";

    public static final String PREFS_FAVNAMES = "FavNames";
    public static final String PREFS_FAVLOCATIONS = "FavLocations";

    public static final String RARLIB = "rarlab_rar";

    public static final String TWO_DOTS = "..";

    public static final int RARX_BADARCHIVE = 5;
    public static final int RARX_CANCEL = 4;
    public static final int RARX_CREATE = 3;
    public static final int RARX_MEMORY = 1;
    public static final int RARX_NONE = 7;
    public static final int RARX_NOTARCHIVE = 6;
    public static final int RARX_OPEN = 2;
    public static final int RARX_SUCCESS = 0;

    public static final int REQ_CODE_ASKNEXTVOLUME = 12;
    public static final int REQ_CODE_ASKREPLACE = 9;
    public static final int REQ_CODE_BENCHMARKDONE = 18;
    public static final int REQ_CODE_BROWSE = 3;
    public static final int REQ_CODE_BROWSE_EXTRFOLDER = 35;
    public static final int REQ_CODE_BROWSE_STARTFOLDER = 34;
    public static final int REQ_CODE_CMDADD = 4;
    public static final int REQ_CODE_CMDCOPY = 20;
    public static final int REQ_CODE_CMDDELETE = 7;
    public static final int REQ_CODE_CMDEXTRACT = 5;
    public static final int REQ_CODE_CMDEXTRACTRUN = 15;
    public static final int REQ_CODE_CMDEXTRACTSEND = 24;
    public static final int REQ_CODE_CMDLIST = 8;
    public static final int REQ_CODE_CMDRENAME = 23;
    public static final int REQ_CODE_CMDTEST = 6;
    public static final int REQ_CODE_CREATENEXTVOLUME = 11;
    public static final int REQ_CODE_EXFILEINFO = 38;
    public static final int REQ_CODE_EXTCARD = 27;
    public static final int REQ_CODE_EXTROPT = 2;
    public static final int REQ_CODE_FAVORITEADD = 14;
    public static final int REQ_CODE_GETARC = 1;
    public static final int REQ_CODE_GETCOMMENT = 26;
    public static final int REQ_CODE_GETMASK = 29;
    public static final int REQ_CODE_GETPASSWORD = 10;
    public static final int REQ_CODE_INFOFILESDONE = 25;
    public static final int REQ_CODE_NEWFOLDERDONE = 19;
    public static final int REQ_CODE_NOTIFIER = 28;
    public static final int REQ_CODE_OPENDOCTREE_COMMAND = 33;
    public static final int REQ_CODE_OPENDOCTREE_MKDIR = 31;
    public static final int REQ_CODE_OPENDOCTREE_RENAME = 32;
    public static final int REQ_CODE_POPUP_LIST = 36;
    public static final int REQ_CODE_PROFILEADD = 13;
    public static final int REQ_CODE_RENAMEDONE = 22;
    public static final int REQ_CODE_REPAIRCONFIRM = 16;
    public static final int REQ_CODE_REPAIRDONE = 17;
    public static final int REQ_CODE_SETTINGSDONE = 21;
    public static final int REQ_CODE_UP_MULTIPLE = 30;
    public static final int REQ_PERMISSION_WRITE = 1;

    public static final int THEME_ACTIVITY = 0;
    public static final int THEME_ACTIVITY_NO_BAR = 1;
    public static final int THEME_DIALOG = 3;
    public static final int THEME_DIALOG_NO_BAR = 4;
    public static final int THEME_DIALOG_WHEN_LARGE = 2;

    public static final int COMPRESSION_METHOD_STORE = 0;
    public static final int COMPRESSION_METHOD_FASTEST = 1;
    public static final int COMPRESSION_METHOD_FAST = 2;
    public static final int COMPRESSION_METHOD_NORMAL = 3;
    public static final int COMPRESSION_METHOD_GOOD = 4;
    public static final int COMPRESSION_METHOD_BEST = 5;

    public static final String EXT_RAR = "rar";
    public static final String EXTDOT_RAR = ".rar";
    public static final String EXT_REV = "rev";
    public static final String EXTDOT_REV = ".rev";
    public static final String EXT_ZIP = "zip";
    public static final String EXTDOT_ZIP = ".zip";

    public static final long MAX_TIME_MILLIS = 5000;
    public static final int MAX_HISTORY_SIZE = 100;
}
