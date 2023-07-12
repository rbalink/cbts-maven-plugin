package com.tub;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

//lifecycle phase in maven
@Mojo(name="cbts", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class CBTSPrototypePlugin extends AbstractMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub
		getLog().info("*** cbts prototype BEGIN ***");
		
		

		getLog().info("*** cbts prototype ENDS ***");
		
	}
	
}