package controller;

import builder.VegetableFactoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import parser.VegetableParamsParserImpl;
import reader.VegetableAttributesReader;
import reader.VegetableAttributesLineReader;
import repository.VegetableRepository;
import service.VegetableService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.MalformedInputException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

@RunWith(JUnit4.class)
public class VegetableControllerImplTest {

    private VegetableControllerImpl vegetableControllerImpl;

    @Before
    public void init() {
        VegetableFactoryService factoryService = new VegetableFactoryService(new VegetableParamsParserImpl());
        VegetableService service = new VegetableService(new VegetableRepository());
        VegetableAttributesReader reader = new VegetableAttributesLineReader();
        vegetableControllerImpl = new VegetableControllerImpl(factoryService, service, reader);
    }

    @Test
    public void transferFileToObjectsOne() throws IOException, URISyntaxException {
        URI uri = this.getClass().getResource("/one_correct_line.txt").toURI();
        String path = Paths.get(uri).toString();
        vegetableControllerImpl.transformFileToVegetables(path);
        Assert.assertEquals(1, vegetableControllerImpl.getAll().size());
    }

    @Test
    public void transferFileToObjectsTwo() throws IOException, URISyntaxException {
        URI uri = this.getClass().getResource("/all_lines_incorrect.txt").toURI();
        String path = Paths.get(uri).toString();
        vegetableControllerImpl.transformFileToVegetables(path);
        Assert.assertEquals(0, vegetableControllerImpl.getAll().size());
    }

    @Test
    public void transferFileToObjectsThree() throws IOException, URISyntaxException {
        URI uri = this.getClass().getResource("/correct_and_incorrect_lines.txt").toURI();
        String path = Paths.get(uri).toString();
        vegetableControllerImpl.transformFileToVegetables(path);
        Assert.assertEquals(4, vegetableControllerImpl.getAll().size());
    }

    @Test
    public void transferFileToObjectsFour() throws IOException, URISyntaxException {
        URI uri = this.getClass().getResource("/empty_file.txt").toURI();
        String path = Paths.get(uri).toString();
        vegetableControllerImpl.transformFileToVegetables(path);
        Assert.assertEquals(0, vegetableControllerImpl.getAll().size());
    }

    @Test(expected = MalformedInputException.class)
    public void transferFileToObjectsFive() throws URISyntaxException, IOException {
        URI uri = this.getClass().getResource("/non_text_file.vp").toURI();
        String path = Paths.get(uri).toString();
        vegetableControllerImpl.transformFileToVegetables(path);
    }

    @Test(expected = NoSuchFileException.class)
    public void transferFileToObjectsSix() throws IOException {
        String path = "dfgghgfhgfhfgk";
        vegetableControllerImpl.transformFileToVegetables(path);
    }

}
