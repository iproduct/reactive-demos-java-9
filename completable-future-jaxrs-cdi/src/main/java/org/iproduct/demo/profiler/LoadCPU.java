package org.iproduct.demo.profiler;

public class LoadCPU {

	public static void main(String[] args) {
		long result = 0;
		for(long n = 0; n < 10000000000L; n++) {
			result += n;
		}
		System.out.println(result);
	}

}
