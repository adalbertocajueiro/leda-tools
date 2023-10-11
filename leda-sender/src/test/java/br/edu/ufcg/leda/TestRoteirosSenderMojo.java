package br.edu.ufcg.leda;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.junit.jupiter.api.Test;

public class TestRoteirosSenderMojo {
    
    @Test
    public void testUploadEnvironment01() throws ProjectBuildingException, MojoExecutionException{ 
        MavenProject mp = new MavenProject();
        mp.getBuild().setDirectory("/Users/adalbertocajueiro/Documents/UFCG/new-leda/new-leda-roteiros/Heap binaria/target");
        mp.getBuild().setSourceDirectory("/Users/adalbertocajueiro/Documents/UFCG/new-leda/new-leda-roteiros/Heap binaria");        
        mp.setArtifactId("Rot-HeapBinaria");

        LEDARoteiroSenderMojo mojo = new LEDARoteiroSenderMojo();
        mojo.setProject(mp);
        mojo.setRoteiro("R12-0X");
        mojo.setSemestre("2023.1");
        mojo.setUrl("http://localhost:8080/ambientes/uploadEnvironment");
        mojo.setUserName("Adalberto Cajueiro de Farias");
        mojo.setDefaultSend(true);
        mojo.execute();
    }
}
