package app.junk;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.xsp.extlib.component.rest.CustomService;
import com.ibm.xsp.extlib.component.rest.CustomServiceBean;

public class DrawerServiceBean extends CustomServiceBean {

	private final String myEndpoint = "junkDrawer";
	private final String contentType = "application/json; charset=UTF-8";

	/**
	 * Enum of all available HTTP methods.
	 */
	private enum HttpMethods {
		GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH
	}


	/**
	 * @return methodsAllowed String[]
	 */
	private String[] getMethodsAllowed() {
		return new String[] { "GET", "POST", "PUT", "DELETE", "OPTIONS" };
	}

	@Override
	public void renderService(CustomService service, RestServiceEngine engine) throws ServiceException {

		HttpServletRequest req = engine.getHttpRequest();
		HttpServletResponse res = engine.getHttpResponse();

		try {

			res.setHeader("Content-Type", this.contentType);
			PrintWriter out = new PrintWriter(res.getOutputStream());

			HttpMethods reqMethod = HttpMethods.valueOf(req.getMethod());
			switch (reqMethod) {
				case GET:
					this.doGet(req, res, out);
					break;
				case POST:
					this.doPost(req, res, out);
					break;
				case PUT:
					this.doPut(req, res, out);
					break;
				case DELETE:
					this.doDelete(req, res, out);
					break;
				case OPTIONS:
					this.doOptions(req, res, out);
					break;
				default:
					this.doUnHandled(req, res, out);
				break;
			}

			out.close();

		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

	/**
	 * GET requests.
	 *
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @param out ServletOutputStream
	 */
	public void doGet( HttpServletRequest req, HttpServletResponse res, PrintWriter out ) {
		boolean err = false;

		Pattern regExIdPattern = Pattern.compile("/" + myEndpoint + "/([0-9]*)");
		String pathInfo = req.getPathInfo();
		Matcher matchRecord = regExIdPattern.matcher(pathInfo);

		Map<String, Object> myResp = new HashMap<String, Object>();
		if( matchRecord.find() ) {
			int id = Integer.parseInt(matchRecord.group(1), 10);
			Map<String, Object> ob = this.calcSomeData(id);
			if( null != ob ) {
				myResp.put("data", ob);
			} else {
				myResp.put("data", null);
				myResp.put("errorMessage", "record not found");
				res.setStatus(404);
			}
		} else {
			ArrayList<Map<String, Object>> ar = this.calcSomeData();
			myResp.put("data", ar);
		}
		myResp.put("error", err);

		JsonJavaFactory factory = JsonJavaFactory.instanceEx;
		String tmpJson = "";
		try {
			tmpJson = JsonGenerator.toJson(factory, myResp);
		} catch (JsonException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(tmpJson);
	}

	/**
	 * POST requests.
	 *
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @param out ServletOutputStream
	 */
	public void doPost( HttpServletRequest req, HttpServletResponse res, PrintWriter out ) {
		// nothing yet
		this.doUnHandled(req, res, out);
	}

	/**
	 * PUT requests.
	 *
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @param out ServletOutputStream
	 */
	public void doPut( HttpServletRequest req, HttpServletResponse res, PrintWriter out ) {
		// nothing yet
		this.doUnHandled(req, res, out);
	}

	/**
	 * DELETE requests.
	 *
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @param out ServletOutputStream
	 */
	public void doDelete( HttpServletRequest req, HttpServletResponse res, PrintWriter out ) {
		// nothing yet
		this.doUnHandled(req, res, out);
	}

	/**
	 * Method for requests methods that are not implemented (e.g.- PATCH, TRACE, etc).
	 *
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @param out ServletOutputStream
	 */
	public void doUnHandled( HttpServletRequest req, HttpServletResponse res, PrintWriter out ) {
		res.setStatus(405); // Method Not Allowed
		res.setHeader("Access-Control-Allow-Methods", Arrays.toString(this
				.getMethodsAllowed()));
	}

	/**
	 * Returns 200 OK with the Access-Control-Allow-Methods header and the
	 * Arrays.toString result of the String[] from getMethodsAllowed().
	 *
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @param out ServletOutputStream
	 */
	public void doOptions( HttpServletRequest req, HttpServletResponse res, PrintWriter out ) {
		res.setStatus(200); // OK
		res.setHeader("Access-Control-Allow-Methods", Arrays.toString(this
				.getMethodsAllowed()));
	}

	private ArrayList<Map<String, Object>> calcSomeData() {
		ArrayList<Map<String, Object>> ar = new ArrayList<Map<String, Object>>();
		for ( int i = 0; i < 100; i++ ) {
			Map<String, Object> ob = new HashMap<String, Object>();
			ob.put("id", i + 1);
			ob.put("someValue", new Double(Math.floor(Math.random() * 100)).intValue());
			ar.add(ob);
		}
		return ar;
	}

	private Map<String, Object> calcSomeData( int id ) {
		try {
			int nwKey = id - 1;
			ArrayList<Map<String, Object>> tmpOb = this.calcSomeData();
			Collections.sort(tmpOb, new ListKeyValComparator());
			return tmpOb.get(nwKey);
		} catch (IndexOutOfBoundsException iobe) {
			return null;
		}
	}

	/**
	 * Custom Comparator, for use with sorting (ascending) a List<SelectItem>.
	 */
	private static class ListKeyValComparator implements Comparator<Map<String, Object>> {
		public int compare(Map<String, Object> s1, Map<String, Object> s2) {
			//you can also do a case sensitive via s1.getLabel().compareTo(s2.getLabel())
			return (Integer) s1.get("id") - (Integer) s2.get("id");
		}
	}

}
