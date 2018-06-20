package gov.doe.jgi.boost.client.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.doe.jgi.boost.enums.Strategy;
import gov.doe.jgi.boost.enums.Vendor;

public class BOOSTConstantsArrayList {

	public static List<Strategy> strategyList = Arrays.asList(Strategy.values());
	public static List<Vendor> vendorList = Arrays.asList(Vendor.values());
	public static boolean[] annotation = {true, false};
}

