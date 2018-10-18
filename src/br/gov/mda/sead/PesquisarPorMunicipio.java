package br.gov.mda.sead;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class PesquisarPorMunicipio {

	public static void main(String[] args) {

		try {

			if (args.length == 0 || args[0] == null || args[0].equals("")) {
				System.err.println("Erro: Digite a <uf> desejada para coleta. Ex.: java -jar selenium-mda.jar AC");
				System.exit(1);
			}

			String uf = args[0];

			System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
			WebDriver driver = new FirefoxDriver();

			BufferedReader br = new BufferedReader(new FileReader("ufs" + "\\" + uf + ".txt"));
			String linha = br.readLine();
			List<String> listaMunicipios = new ArrayList<String>();
			while (linha != null) {
				listaMunicipios.add(linha);
				linha = br.readLine();
			}
			br.close();

			for (String municipio : listaMunicipios) {

				// Acessa o site e maximiza a tela
				String baseUrl = "http://smap14.mda.gov.br/extratodap/PesquisarDAP";
				driver.get(baseUrl);
				driver.manage().window().maximize();

				// Pesquisa por Município
				driver.findElement(By.xpath("//button[@class='form-control btn btn-success']")).click();
				driver.findElement(By.xpath("//a[@href='#Municipio']")).click();
				Thread.sleep(2000);

				// Seleciona a UF passada como parâmetro
				driver.findElement(By.xpath("//option[@value='" + Util.UF.valueOf(uf).getValor() + "']")).click();
				Thread.sleep(3000);
				// Seleciona o município
				new Select(driver.findElement(By.id("ddlMunicipio"))).selectByVisibleText(municipio);

				// Preenche o campo captcha
				String captcha_hidden = driver.findElement(By.xpath("//input[@id='h_tbCaptchaMunicipio']"))
						.getAttribute("value");
				driver.findElement(By.xpath("//input[@id='m_tbCaptchaMunicipio']")).sendKeys(captcha_hidden);
				Thread.sleep(3000);

				// Seleciona o botão de Pesquisar
				driver.findElements(By.id("btnPesquisarMunicipio")).get(2).click();

				while (!driver.findElement(By.id("gridPessoaFisica")).isDisplayed()) {
				}
				Thread.sleep(3000);

				File arquivoMunicipio = new File("municipios" + "\\" + uf + "\\" + municipio + ".csv");
				arquivoMunicipio.getParentFile().mkdirs();

				BufferedWriter writer = null;
				CSVPrinter csvPrinter = null;
				try {
					writer = Files.newBufferedWriter(Paths.get(arquivoMunicipio.getPath()));

					csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withHeader("txtNDAP", "txtCPFTitular1",
							"txtNomeTitular1", "txtCPFTitular2", "txtNomeTitular2"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				List<WebElement> rows = driver
						.findElements(By.xpath("//table[@id='gridPessoaFisica']/tbody/tr/td/a/button"));
				String registros = driver.findElement(By.id("gridPessoaFisica_info")).getText();
				registros = registros.replace("Mostrando de 1 até ", "");
				registros = registros.substring(registros.indexOf("de ") + 3, registros.indexOf("registros"));
				registros = registros.replaceAll(",", "").trim();
				int contador = 0;

				while (contador < Integer.parseInt(registros)) {
					if ((contador > 0) && (contador % 10 == 0)) {
						if (driver.findElement(By.id("gridPessoaFisica_next")).isEnabled()) {
							driver.findElement(By.id("gridPessoaFisica_next")).click();
							rows = driver
									.findElements(By.xpath("//table[@id='gridPessoaFisica']/tbody/tr/td/a/button"));
						}
					}
					// CarregarExtratoDAP
					try {
						rows.get(contador % 10).click();

						Thread.sleep(1000);
						ArrayList<String> tabs_windows = new ArrayList<String>(driver.getWindowHandles());
						driver.switchTo().window(tabs_windows.get(1));
						driver.getWindowHandle();
						Thread.sleep(3000);
						String txtNDAP = driver.findElement(By.id("txtNDAP")).getAttribute("value");
						String txtCPFTitular1 = driver.findElement(By.id("txtCPFTitular1")).getAttribute("value");
						String txtNomeTitular1 = driver.findElement(By.id("txtNomeTitular1")).getAttribute("value");
						String txtCPFTitular2 = driver.findElement(By.id("txtCPFTitular2")).getAttribute("value");
						String txtNomeTitular2 = driver.findElement(By.id("txtNomeTitular2")).getAttribute("value");
//						Thread.sleep(2000);
						csvPrinter.printRecord(txtNDAP, txtCPFTitular1, txtNomeTitular1, txtCPFTitular2,
								txtNomeTitular2);
//						Thread.sleep(1000);
						csvPrinter.flush();
						driver.close();
						driver.switchTo().window(tabs_windows.get(0));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (IndexOutOfBoundsException e) {

					}
					
					contador++;
				}
				System.out.println("Dados do municipio: " + municipio + " coletados com sucesso.");
			}

			System.out.println("Dados da UF: " + uf + " coletados com sucesso.");

			driver.close();

		} catch (Exception e) {
			System.err.println("Erro na coleta de dados!");
			e.printStackTrace();
		} finally {

		}

	}

}