/**
 * This is a RestController class for exposing the REST endpoints.
 *
 * @author Avinash Tingre
 */
package com.avinash.HotfixService.Controller;

import com.avinash.HotfixService.HotfixviewerApplication;
import com.avinash.HotfixService.Model.*;
import com.avinash.HotfixService.Service.DatabaseLogHandler;
import com.avinash.HotfixService.Service.ECPLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/HFLogViewer")
@Tag(name = "Hotfix Search", description = "API for fetching hotfixes")
public class EcpLogController {
    private static final Logger LOG = LoggerFactory.getLogger(EcpLogController.class);

    @Autowired
    private ECPLogService ecpService;

    @Value("${headerPrefix}")
    private String headerPrefix;

    @Autowired
    private DatabaseLogHandler dbHandler;

    /**
     * REST endpoint for getting "Pageable" results matching below parameters.
     *
     * @param page_no                 (mandatory)
     * @param page_size               (mandatory)
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
     * @param request
     * @return ECPLog objects
     */
    @RequestMapping(value = "/getPageableResult", method = RequestMethod.GET)
    public ResponseEntity<SearchResultMetadata> getPageableResult(
            @RequestParam(value = "page_no", required = true) int page_no,
            @RequestParam(value = "page_size", required = true) int page_size,
            @RequestParam(value = "ecpNo", defaultValue = "", required = false) String ecpNo,
            @RequestParam(value = "description", defaultValue = "", required = false) String description,
            @RequestParam(value = "cramerVersion", defaultValue = "", required = false) List<String> cramerVersion,
            @RequestParam(value = "latestEcp", defaultValue = "", required = false) String latestEcp,
            @RequestParam(value = "requestor", defaultValue = "", required = false) String requestor,
            @RequestParam(value = "fixedBy", defaultValue = "", required = false) String fixedBy,
            @RequestParam(value = "module", defaultValue = "", required = false) List<String> module,
            @RequestParam(value = "caseOrCrNo", defaultValue = "", required = false) String caseOrCrNo,
            @RequestParam(value = "filesModifiedInPerforce", defaultValue = "", required = false) String filesModifiedInPerforce,
            @RequestParam(value = "filesReleasedToCustomer", defaultValue = "", required = false) String filesReleasedToCustomer,
            @RequestParam(value = "rolledIntoVersion", defaultValue = "", required = false) String rolledIntoVersion,
            @RequestParam(value = "specificFunc", defaultValue = "", required = false) String specificFunc,
            HttpServletRequest request, @RequestHeader("Hostname") String hostname,
            @RequestHeader("HostAddress") String HostAddress,
            @RequestHeader("NTNET") String ntnet) {
        LOG.info("Test messa2");
        List<String> requestInput = new ArrayList<String>();
        requestInput.add("pageNo=" + page_no);
        requestInput.add("pageSize=" + page_size);
        requestInput.add("ecpNo=" + ecpNo);
        requestInput.add("description=" + description);
        requestInput.add("latestEcp=" + latestEcp);
        requestInput.add("requestor=" + requestor);
        requestInput.add("fixedBy=" + fixedBy);
        requestInput.add("caseOrCRNo=" + caseOrCrNo);
        requestInput.add("filesModifiedInPerforce=" + filesModifiedInPerforce);
        requestInput.add("filesReleasedToCustomer=" + filesReleasedToCustomer);
        requestInput.add("specificFunc=" + specificFunc);
        requestInput.add("module=" + module);
        requestInput.add("cramerVersion=" + cramerVersion);

        logToDatabase(hostname, HostAddress, ntnet, requestInput, "/getPageableResult");


        if (cramerVersion.isEmpty()) {
            cramerVersion = HotfixviewerApplication.distinctVersion;
        }
        if (module.isEmpty()) {
            module = HotfixviewerApplication.distinctModules;
        }

        List<ECPLog> ecp_list = ecpService.getResultByFields(ecpNo, description, cramerVersion, latestEcp, requestor,
                fixedBy, module, caseOrCrNo, filesModifiedInPerforce, filesReleasedToCustomer, rolledIntoVersion,
                specificFunc, page_no, page_size);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, headerPrefix);

        SearchResultMetadata ro = new SearchResultMetadata();
        ro.setCount(ecp_list.size());
        ro.setDetails(ecp_list);

        return ResponseEntity.ok().headers(headers).body(ro);

    }

    /**
     * REST endpoint for getting "all" results matching below parameters.
     *
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
     * @param httpRequest
     * @return
     */
    @Operation(summary = "Find all hotfixes", description = "Hotfix search with given criteria.", tags = {"Hotfix Search"})
    @RequestMapping(value = "/getAllResults", method = RequestMethod.GET)
    public ResponseEntity<SearchResultMetadata> getAllResults(
            @RequestParam(value = "ecpNo", defaultValue = "", required = false) String ecpNo,
            @RequestParam(value = "description", defaultValue = "", required = false) String description,
            @RequestParam(value = "cramerVersion", defaultValue = "", required = false) List<String> cramerVersion,
            @RequestParam(value = "latestEcp", defaultValue = "", required = false) String latestEcp,
            @RequestParam(value = "requestor", defaultValue = "", required = false) String requestor,
            @RequestParam(value = "fixedBy", defaultValue = "", required = false) String fixedBy,
            @RequestParam(value = "module", defaultValue = "", required = false) List<String> module,
            @RequestParam(value = "caseOrCrNo", defaultValue = "", required = false) String caseOrCrNo,
            @RequestParam(value = "filesModifiedInPerforce", defaultValue = "", required = false) String filesModifiedInPerforce,
            @RequestParam(value = "filesReleasedToCustomer", defaultValue = "", required = false) String filesReleasedToCustomer,
            @RequestParam(value = "rolledIntoVersion", defaultValue = "", required = false) String rolledIntoVersion,
            @RequestParam(value = "specificFunc", defaultValue = "", required = false) String specificFunc,
            HttpServletRequest httpRequest, @RequestHeader(value = "Hostname", defaultValue = "disabled", required = false) String hostname,
            @RequestHeader(value = "HostAddress", defaultValue = "disabled", required = false) String HostAddress,
            @RequestHeader(value = "NTNET", defaultValue = "disabled", required = false) String ntnet) {

        Long t1 = new Date().getTime();

        List<String> requestInput = new ArrayList<String>();

        if (ecpNo.length() > 0) requestInput.add("Hotfix No: " + ecpNo + ", ");
        if (latestEcp.length() > 0) requestInput.add("Latest Hotfix: " + latestEcp + ", ");
        if (description.length() > 0) requestInput.add("Description: " + description + ", ");
        if (cramerVersion.size() > 0) requestInput.add("Versions: " + cramerVersion + ", ");
        if (requestor.length() > 0) requestInput.add("Requested by: " + requestor + ", ");
        if (fixedBy.length() > 0) requestInput.add("Fixed by: " + fixedBy + ", ");
        if (module.size() > 0) requestInput.add("Modules: " + module + ", ");
        if (caseOrCrNo.length() > 0) requestInput.add("Case or CR No: " + caseOrCrNo + ", ");
        if (filesModifiedInPerforce.length() > 0) requestInput.add("Files modified: " + filesModifiedInPerforce + ", ");
        if (filesReleasedToCustomer.length() > 0) requestInput.add("Files released: " + filesReleasedToCustomer + ", ");
        if (specificFunc.length() > 0) requestInput.add("Specific function: " + specificFunc + ", ");

        logToDatabase(hostname, HostAddress, ntnet, requestInput, "/getAllResults");

        if (cramerVersion.isEmpty()) {
            cramerVersion = HotfixviewerApplication.distinctVersion;
        }
        if (module.isEmpty()) {
            module = HotfixviewerApplication.distinctModules;
        }

        List<ECPLog> ecp_list = ecpService.getResultByFields(ecpNo, description, cramerVersion, latestEcp, requestor,
                fixedBy, module, caseOrCrNo, filesModifiedInPerforce, filesReleasedToCustomer, rolledIntoVersion,
                specificFunc);

        SearchResultMetadata ro = new SearchResultMetadata();
        ro.setCount(ecp_list.size());
        ro.setDetails(ecp_list);


        Long t2 = new Date().getTime();
        System.out.println("Time taken for getResultByFields: "+ (t2-t1));


        return ResponseEntity.ok().body(ro);

    }

    @RequestMapping(value = "/getTotalCountAllResults", method = RequestMethod.GET)
    public ResponseEntity<SearchResultMetadata> getTotalCountAllResults(
            @RequestParam(value = "ecpNo", defaultValue = "", required = false) String ecpNo,
            @RequestParam(value = "description", defaultValue = "", required = false) String description,
            @RequestParam(value = "cramerVersion", defaultValue = "", required = false) List<String> cramerVersion,
            @RequestParam(value = "latestEcp", defaultValue = "", required = false) String latestEcp,
            @RequestParam(value = "requestor", defaultValue = "", required = false) String requestor,
            @RequestParam(value = "fixedBy", defaultValue = "", required = false) String fixedBy,
            @RequestParam(value = "module", defaultValue = "", required = false) List<String> module,
            @RequestParam(value = "caseOrCrNo", defaultValue = "", required = false) String caseOrCrNo,
            @RequestParam(value = "filesModifiedInPerforce", defaultValue = "", required = false) String filesModifiedInPerforce,
            @RequestParam(value = "filesReleasedToCustomer", defaultValue = "", required = false) String filesReleasedToCustomer,
            @RequestParam(value = "rolledIntoVersion", defaultValue = "", required = false) String rolledIntoVersion,
            @RequestParam(value = "specificFunc", defaultValue = "", required = false) String specificFunc,
            HttpServletRequest httpRequest, @RequestHeader(value = "Hostname", defaultValue = "disabled", required = false) String hostname,
            @RequestHeader(value = "HostAddress", defaultValue = "disabled", required = false) String HostAddress,
            @RequestHeader(value = "NTNET", defaultValue = "disabled", required = false) String ntnet) {
        Long t3 = new Date().getTime();


        if (cramerVersion.isEmpty()) {
            cramerVersion = HotfixviewerApplication.distinctVersion;
        }
        if (module.isEmpty()) {
            module = HotfixviewerApplication.distinctModules;
        }

        Long result = ecpService.getRecordCount(ecpNo, description, cramerVersion, latestEcp, requestor,
                fixedBy, module, caseOrCrNo, filesModifiedInPerforce, filesReleasedToCustomer, rolledIntoVersion,
                specificFunc);



        SearchResultMetadata resultObject = new SearchResultMetadata();
        resultObject.setCount( result.intValue());
        resultObject.setDetails(null);


        Long t4 = new Date().getTime();
        System.out.println("Time taken for getRecordCount: "+ (t4-t3));
        System.out.println("Count: "+result);

        return ResponseEntity.ok().body(resultObject);

    }

    private void logToDatabase(String hostname, String hostaddress, String ntnet,
                               List<String> searchInput, String requestName) {

        try {

            UserDetails userDetails = new UserDetails();
            userDetails.setDate(new Date());
            userDetails.setRequestPath(requestName);
            userDetails.setSearchInput(searchInput);
            userDetails.setHostaddress(hostaddress);
            userDetails.setHostname(hostname);
            userDetails.setNtnet(ntnet);


            dbHandler.addUserDetails(userDetails);
            LOG.info("Saved user details to database.");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("Exception occurred while logging to database.");
        }


    }

    /**
     * REST endpoint for storing records from excel into DB. It will delete existing
     * records and add new records.
     */

    @RequestMapping(value = "/getUnderlyingHFs", method = RequestMethod.GET)
    public ResponseEntity<UnderlyingHFMetadata> getUnderlyingHFs(
            @RequestParam(value = "latestEcp", defaultValue = "-", required = true) String latestEcp,
            HttpServletRequest request) {

        // To restrict the requests only from specific hosts, Uncomment the below line.
        // if (customConfig.getAllowedHosts().contains(client)) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, headerPrefix);

        Map<Integer, String> map = ecpService.getUnderlyingHF(latestEcp);
        UnderlyingHFMetadata ro = new UnderlyingHFMetadata();
        ro.setCount(map.size());
        ro.setDetails(map);



        return ResponseEntity.ok().headers(headers).body(ro);

    }

    @RequestMapping(value = "/getDistinctCramerVersions", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getDistinctCramerVersions(HttpServletRequest request) {

        // To restrict the requests only from specific hosts, Uncomment the below line.
        // if (customConfig.getAllowedHosts().contains(client)) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, headerPrefix);

        List<String> result = HotfixviewerApplication.distinctVersion;

        return ResponseEntity.ok().headers(headers).body(result);

    }

    @RequestMapping(value = "/getSummary", method = RequestMethod.GET)
    public HotfixSummary getDatabaseSummary() {
        return dbHandler.getSummary();
    }

    @RequestMapping(value = "/getUserDetails", method = RequestMethod.GET)
    public List<UserDetails> getUserDetails(
            @RequestParam(value = "host", defaultValue = "--", required = false) String host) {

        return dbHandler.getUserDetails(host);
    }

    @RequestMapping(value = "/getDistinctModules", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getDistinctModules(HttpServletRequest request) {

        // To restrict the requests only from specific hosts, Uncomment the below line.
        // if (customConfig.getAllowedHosts().contains(client)) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, headerPrefix);

        List<String> result = HotfixviewerApplication.distinctModules;

        return ResponseEntity.ok().headers(headers).body(result);

    }

}