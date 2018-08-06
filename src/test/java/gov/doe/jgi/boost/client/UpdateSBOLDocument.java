package gov.doe.jgi.boost.client;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;

import gov.doe.jgi.boost.client.constants.BOOSTClientConfigs;
import gov.doe.jgi.boost.client.constants.LoginCredentials;
import gov.doe.jgi.boost.client.utils.UpdateDocumentUtils;
import gov.doe.jgi.boost.enums.FileFormat;
import gov.doe.jgi.boost.enums.Strategy;
import gov.doe.jgi.boost.exception.BOOSTBackEndException;
import gov.doe.jgi.boost.exception.BOOSTClientException;

public class UpdateSBOLDocument {

	public static final String boostNamespace = "https://boost.jgi.doe.gov/";

	/**
	 * 
	 * @param filename
	 * 
	 * @return
	 * 
	 * @throws BOOSTClientException
	 * @throws BOOSTBackEndException
	 * @throws IOException
	 * @throws SBOLValidationException
	 * @throws SBOLConversionException
	 */
	public static String doCodonJuggling(final String filename) 
			throws BOOSTClientException, BOOSTBackEndException, IOException, SBOLValidationException, SBOLConversionException {
		
		String outputFilename = (String)null;
		
		BOOSTClient client = new BOOSTClient(LoginCredentials.mJWT);
		// set the target namespace
		BOOSTClientConfigs.SBOL_TARGET_NAMESPACE = "https://boost.jgi.doe.gov/";
		
		// we store all submitted jobs in a hash-set
		Set<String> jobUUIDs = new HashSet<String>();

		// codon juggle (using a SBOL file)
		String codonJuggleJobUUID1 = client.codonJuggle(
				filename,									// input sequences
				BOOSTClientConfigs.SBOL_TARGET_NAMESPACE,	// the target namespace
				false,					 					// exclusively 5'-3' coding sequences 
				Strategy.Balanced,		  					// codon selection strategy
				"Saccharomyces cerevisiae",   				// predefined host
				FileFormat.SBOL);		  					// output format
		if(null != codonJuggleJobUUID1) {
			jobUUIDs.add(codonJuggleJobUUID1);
		}
		
		// for all jobs, we check their status
		for(String jobUUID : jobUUIDs) {
			
			JSONObject jobReport = null;
			while(null == (jobReport = client.getJobReport(jobUUID))) {
				try {
					Thread.sleep(2000);
				} catch(Exception e) {}
			}

			java.nio.file.Path jobOutputDir = Paths.get(".", "data", "out", jobUUID);
			if(!Files.exists(jobOutputDir)) {
				Files.createDirectories(jobOutputDir);

				// write the response to a file
				FileUtils.writeStringToFile(
						Paths.get(jobOutputDir.toString(), "response-" + jobUUID + ".json").toFile(), 
						jobReport.toString(4));

				// write the input and output files
				if(jobReport.has("response")) {
					
					JSONArray responseArray = jobReport.getJSONArray("response");
					
					Iterator<Object> it = responseArray.iterator();
					while(it.hasNext()) {
						Object responseObject = it.next();
						if(responseObject instanceof JSONObject) {
							JSONObject jsonResponseObject = (JSONObject)responseObject;
							
							// input sequences
							FileUtils.writeStringToFile(
								Paths.get(jobOutputDir.toString(), jobUUID + "-input-file.sbol.xml").toFile(),
								jsonResponseObject.getString("original-sequences-text"));

							outputFilename = Paths.get(jobOutputDir.toString(), jobUUID + "-output-file.sbol.xml").toString();
							// output sequences
							FileUtils.writeStringToFile(
								Paths.get(jobOutputDir.toString(), jobUUID + "-output-file.sbol.xml").toFile(),
								jsonResponseObject.getString("modified-sequences-text"));
						}
					}
				}
				
				// write just the output SBOL document to a file
				if(jobReport.has("provenance")) {

					// get the SBOL document from the JSON response
					JSONObject jsonProvenance = jobReport.getJSONObject("provenance");

					// write to input SBOLdocument (input to BOOST) to a file
					String boostProvInputDocument = jsonProvenance.getString("input-document");
					FileUtils.writeStringToFile(
							Paths.get(jobOutputDir.toString(), jobUUID + "-input-document.sbol.xml").toFile(), 
							boostProvInputDocument);

					// write to output SBOLdocument (generated by BOOST) to a file
					String boostProvOutputDocument = jsonProvenance.getString("output-document");
					FileUtils.writeStringToFile(
							Paths.get(jobOutputDir.toString(), jobUUID + "-output-document.sbol.xml").toFile(), 
							boostProvOutputDocument);
					
//					System.out.println(Paths.get(jobOutputDir.toString(), jobUUID + "-output-document.sbol.xml").toFile());
				}
			
			}
		}
		
		return outputFilename;
	}

	/**
	 * 
	 * @param directory
	 * 
	 * @throws SBOLValidationException
	 * @throws IOException
	 * @throws SBOLConversionException
	 * @throws BOOSTClientException
	 * @throws BOOSTBackEndException
	 */
	public static void design(final String directory) 
			throws SBOLValidationException, IOException, SBOLConversionException, BOOSTClientException, BOOSTBackEndException {
		
		// the name of the file that contains the SBOLDocument, which is the output of SBOLDesigner
		String sbolDesignerDocumentFilename = directory + "/01-sbol-designer-output.sbol.xml";

		// call BOOST's API to do codon-juggling
		String outputFilename = doCodonJuggling(sbolDesignerDocumentFilename);
		
		// copy the file from the BOOST client's job-directory
		// to the test-directory
		FileUtils.copyFile(Paths.get(outputFilename).toFile(), Paths.get(directory, "02-boost-output.sbol.xml").toFile());
		
		// read the SBOLDocument that is being returned by BOOST
		SBOLDocument document = SBOLReader.read(outputFilename);

		// ------------------------------------------------------------------------------- 
		// UPDATE THE DOCUMENT
		// -- wasDerivedFrom
		// -- sequences
		Map<URI, URI> updatedURIs = UpdateDocumentUtils.getMapOfUpdatedURIs(document);
		UpdateDocumentUtils.updateDocument(document, updatedURIs, boostNamespace);
		// ------------------------------------------------------------------------------- 

		// the final document that the BOOST-Client should return
		
		// serialize the updated document to a file
		SBOLWriter.write(document, Paths.get(directory, "03-boost-client-output.sbol.xml").toFile());
	}

	
	public static void main(String[] args) 
			throws SBOLValidationException, IOException, SBOLConversionException, BOOSTClientException, BOOSTBackEndException {

		File[] directories = listSubDirectories("./data/test/codon-juggle/");
		for(File directory : directories) {
			
			if(!directory.toString().endsWith("01-single-cds")) { continue; }
//			if(!directory.toString().endsWith("05-transcriptional-unit")) { continue; }
			
			design(directory.toString());
		}
	}
	
	public static File[] listSubDirectories(final String directory) {
		return new File(directory).listFiles(
				new FileFilter() {
		    @Override
		    public boolean accept(File file) {
		        return file.isDirectory();
		    }
		});
	}

}
