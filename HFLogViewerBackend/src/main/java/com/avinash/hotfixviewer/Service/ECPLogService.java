/**
 * This is a Service class. Methods from this class are mainly called by REST Controller.
 * @author Avinash Tingre
 */
package com.avinash.hotfixviewer.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.avinash.hotfixviewer.Model.ECPLog;
import com.avinash.hotfixviewer.Repository.ECPLogRepository;

@Component
public class ECPLogService {
	private static final Logger LOG = LoggerFactory.getLogger(ECPLogService.class);

	@Autowired
	ECPLogRepository ecpRepo;
	
	/**
	 * Method for getting "pageable" results from Database with below matching parameters.
	 * @param ecpNo
	 * @param description
	 * @param cramerVersion
	 * @param latestEcp
	 * @param requestor
	 * @param fixedBy
	 * @param module
	 * @param caseOrCrNo
	 * @param filesModifiedInPerforce
	 * @param filesReleasedToCustomer
	 * @param rolledIntoVersion
	 * @param specificFunc
	 * @param page_no
	 * @param page_size
	 * @return List<ECPLog>
	 */
	@SuppressWarnings("unchecked")
	public List<ECPLog> getResultByFields(String ecpNo, String description, List<String> cramerVersion, String latestEcp,
			String requestor, String fixedBy, List<String> module, String caseOrCrNo, String filesModifiedInPerforce,
			String filesReleasedToCustomer, String rolledIntoVersion, String specificFunc, int page_no, int page_size) {
		try {
			
			ecpNo = ecpNo.replaceAll("\\s", ".*");
			description = description.replaceAll("\\s", ".*");
			latestEcp = latestEcp.replaceAll("\\s", ".*");
			requestor = requestor.replaceAll("\\s", ".*");
			fixedBy = fixedBy.replaceAll("\\s", ".*");
			caseOrCrNo = caseOrCrNo.replaceAll("\\s", ".*");
			filesModifiedInPerforce = filesModifiedInPerforce.replaceAll("\\s", ".*");
			filesReleasedToCustomer = filesReleasedToCustomer.replaceAll("\\s", ".*");
			rolledIntoVersion = rolledIntoVersion.replaceAll("\\s", ".*");
			specificFunc = specificFunc.replaceAll("\\s", ".*");
			
			
			List<ECPLog> result = ecpRepo.findByOptionsWithPaging(".*" + ecpNo + ".*", ".*" + description + ".*",
					cramerVersion, ".*" + latestEcp + ".*", ".*" + requestor + ".*", ".*" + fixedBy + ".*",
					module, ".*" + caseOrCrNo + ".*", ".*" + filesModifiedInPerforce + ".*",
					".*" + filesReleasedToCustomer + ".*", ".*" + rolledIntoVersion + ".*", ".*" + specificFunc + ".*",
					PageRequest.of(page_no, page_size));
			Collections.sort(result);
			return result;
		} catch (Exception e) {
			LOG.info("Exception:\n" + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Method for getting all results from Database with below matching parameters.
	 * @param ecpNo
	 * @param description
	 * @param cramerVersion
	 * @param latestEcp
	 * @param requestor
	 * @param fixedBy
	 * @param module
	 * @param caseOrCrNo
	 * @param filesModifiedInPerforce
	 * @param filesReleasedToCustomer
	 * @param rolledIntoVersion
	 * @param specificFunc
	 * @return List<ECPLog>
	 */
	@SuppressWarnings("unchecked")
	public List<ECPLog> getResultByFields(String ecpNo, String description, List<String> cramerVersion,
			String latestEcp, String requestor, String fixedBy, List<String> module, String caseOrCrNo,
			String filesModifiedInPerforce, String filesReleasedToCustomer, String rolledIntoVersion,
			String specificFunc) {
		try {
			
			ecpNo = ecpNo.replaceAll("\\s", ".*");
			description = description.replaceAll("\\s", ".*");
			latestEcp = latestEcp.replaceAll("\\s", ".*");
			requestor = requestor.replaceAll("\\s", ".*");
			fixedBy = fixedBy.replaceAll("\\s", ".*");
			caseOrCrNo = caseOrCrNo.replaceAll("\\s", ".*");
			filesModifiedInPerforce = filesModifiedInPerforce.replaceAll("\\s", ".*");
			filesReleasedToCustomer = filesReleasedToCustomer.replaceAll("\\s", ".*");
			rolledIntoVersion = rolledIntoVersion.replaceAll("\\s", ".*");
			specificFunc = specificFunc.replaceAll("\\s", ".*");
			
			List<ECPLog> result = ecpRepo.findByOptions(".*" + ecpNo + ".*", ".*" + description + ".*", cramerVersion,
					".*" + latestEcp + ".*", ".*" + requestor + ".*", ".*" + fixedBy + ".*", module,
					".*" + caseOrCrNo + ".*", ".*" + filesModifiedInPerforce + ".*",
					".*" + filesReleasedToCustomer + ".*", ".*" + rolledIntoVersion + ".*", ".*" + specificFunc + ".*");
			Collections.sort(result);
			
			
			return result;
		} catch (Exception e) {
			LOG.info("Exception:\n" + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	
	public ECPLog addECP(ECPLog ecp_obj) {

		return ecpRepo.save(ecp_obj);
	}

	public void deleteAllRecords() {
		ecpRepo.deleteAll();
	}

	public List<ECPLog> addAllECPRecords(Iterable<ECPLog> ecp_list) {
		return ecpRepo.saveAll(ecp_list);
	}
	
	/**
	 * Method for getting all records from Database.
	 * @return List of ECPLog
	 */
	public List<ECPLog> getAllECP() {
		if (ecpRepo.findAll().isEmpty())
			return null;
		return ecpRepo.findAll();
	}
	
	public Map<Integer, String> getUnderlyingHF(String latestEcp){
		List<ECPLog> ecp = ecpRepo.findByLatestEcp(latestEcp);
		Map<Integer, String> result_map= new TreeMap<Integer, String>(Collections.reverseOrder());
		
		for(ECPLog e : ecp) {
			try {
				result_map.put(Integer.valueOf(e.getSequence()), e.getEcpNo());				
			}catch(Exception ex) {
				result_map.put(-1, e.getEcpNo());
			}
		}
		return result_map;
	}

	public List<String> modifyListValues(List<String> values){
		List<String> result = new ArrayList<String>();
		
		for (String s : values) {
			s = "/"+s+"/i";
			result.add(s);
		}
		
		return result;
	}
	
	/**
	 * Method for getting disting values from database.
	 * (cramerVersion, modules)
	 * @return Distinct values.
	 */
	public Map<Set<String>, Set<String>> getDistinctValues() {
		Set<String> versions = new HashSet<String>();
		Set<String> modules = new HashSet<String>();
		
		Map<Set<String>, Set<String>> result_list = new HashMap<Set<String>, Set<String>>();
		
		try{
			List<ECPLog> all_ecp = this.getAllECP();
			for (ECPLog ecp : all_ecp) {
				versions.add(ecp.getCramerVersion());
				modules.add(ecp.getModule());
			}
			
			result_list.put(versions, modules);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return result_list;
	}
}