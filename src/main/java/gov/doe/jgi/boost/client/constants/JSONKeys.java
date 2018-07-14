package gov.doe.jgi.boost.client.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Ernst Oberortner
 */
public class JSONKeys {

	/*------------------------
	 * KEY NAMES OF REQUESTS
	 *------------------------*/

	//--------------------------------------------------
	// SYNTHESIS CONSTRAINTS
	public static final String CONSTRAINTS_INFORMATION = "constraints";
	public static final String VENDOR_NAME = "vendor";
	public static final String CONSTRAINTS_TEXT = "text";
	//--------------------------------------------------
	
	public static final String MODIFIED_SEQUENCES_FILE = "modified-sequences-file";
	public static final String MODIFIED_SEQUENCES_TEXT = "modified-sequences-text";
	public static final String TARGET_NAMESPACE = "target-namespace";
	
	// sequence information related keys
	public static final String SEQUENCE_INFORMATION = "sequences";
	public static final String SEQUENCE_TYPE = "type";
	public static final String AUTO_ANNOTATE = "auto-annotate";
	
	// pattern-related keys
	public static final String PATTERN_INFORMATION = "patterns";
	
	// modification-related keys
	public static final String MODIFICATION_INFORMATION = "modifications";
	public static final String STRATEGY_NAME = "strategy";
	public static final String STRATEGY_CODON_USAGE_TABLE = "codon-usage-table";
	public static final String STRATEGY_GENETIC_CODE = "genetic-code";

	// partitioning-related keys
	public static final String PARTITIONING_INFORMATION = "partitioning-parameters";
	public static final String MIN_BB_LENGTH = "min-BB-length";
	public static final String MAX_BB_LENGTH = "max-BB-length";
	public static final String MIN_OVERLAP_GC = "min-overlap-GC";
	public static final String OPT_OVERLAP_GC = "opt-overlap-GC";
	public static final String MAX_OVERLAP_GC = "max-overlap-GC";
	public static final String MIN_OVERLAP_LENGTH = "min-overlap-length";
	public static final String OPT_OVERLAP_LENGTH = "opt-overlap-length";
	public static final String MAX_OVERLAP_LENGTH = "max-overlap-length";
	public static final String FIVE_PRIME_VECTOR_OVERLAP = "5-prime-vector-overlap";
	public static final String THREE_PRIME_VECTOR_OVERLAP = "3-prime-vector-overlap";
	public static final String BATCH = "batch";

	public static final String MIN_PRIMER_LENGTH = "min-primer-length";
	public static final String MAX_PRIMER_LENGTH = "max-primer-length";
	public static final String MAX_PRIMER_TM = "primer-tm";

	// output-related keys
	public static final String OUTPUT_INFORMATION = "output";
	public static final String OUTPUT_FORMAT = "format";
	public static final String OUTPUT_FILNAME = "filename";
	public static final String OUTPUT_LOG_FILENAME = "log-filename";
	
	// file-upload vs. copy-paste entry of information
	public static final String FILE = "file";
	public static final String TEXT = "text";
	public static final String SAVE_AS = "save-as";

	public static final String JOB_INFORMATION = "job";
	public static final String JOB_USERDEFINED_ID = "job-userdefined-id";
	public static final String JOB_BOOST_FUNCTION = "job-BOOST-function";
	public static final String JOB_UUID = "job-uuid";
	public static final String JOB_REPORT = "job-report";
	

	public static final String SEQUENCE_PATTERNS = "patterns";
	public static final String SEQUENCE_PATTERNS_TEXT = "text";

	// response
	public static final String STATUS = "status";
	public static final String STATUS_ERROR = "error";
	public static final String STATUS_OK = "ok";
	
	public static final String TOKEN = "boost-jwt";

	/*------------------------
	 * KEY NAMES FOR RESPONSE
	 *------------------------*/
	public static final String INVALID_SEQUENCE_FORMAT = "invalid";

	/*-------------------------------------
	 * UTILITY DATA STRUCTURES AND METHODS
	 *-------------------------------------*/
	
	public static Map<String, String> polisherArgs;
	
	// initialize the polisherArgs
	{
		polisherArgs = new HashMap<String, String>();
		
		// auto annotations
		polisherArgs.put("auto-annotate", "--auto-annotate");
	}
}
