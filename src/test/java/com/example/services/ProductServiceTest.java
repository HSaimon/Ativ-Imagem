package com.example.services;
import com.example.entities.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;
class ProductServiceTest {
    private ProductService productService;
    @TempDir
    Path tempStorageDir;
    @TempDir
    Path tempSourceDir;
    private Path dummySourceImagePathPng;
    private Path dummySourceImagePathJpg;
    private Path dummySourceImagePathNoExt;
    private Path dummySourceImagePathWithSpace;
    @BeforeEach
    void setUp() throws IOException {
        productService = new ProductService(tempStorageDir.toString());
        dummySourceImagePathPng = tempSourceDir.resolve("source_image_1.png");
        Files.createFile(dummySourceImagePathPng);
        dummySourceImagePathJpg = tempSourceDir.resolve("source_image_2.jpg");
        Files.createFile(dummySourceImagePathJpg);
        dummySourceImagePathNoExt = tempSourceDir.resolve("source_image_no_ext");
        Files.createFile(dummySourceImagePathNoExt);
        dummySourceImagePathWithSpace = tempSourceDir.resolve("source image with space.png");
        Files.createFile(dummySourceImagePathWithSpace);
    }
    @Test
    @DisplayName("Deve salvar a imagem corretamente com nome original")
    void deveSalvarImagemCorretamenteComNomeOriginal() {
        Product product = new Product(1, "Test Product PNG", 10.0f, dummySourceImagePathPng.toString());
        Path expectedDestinationPath = tempStorageDir.resolve("1_source_image_1.png");
        boolean result = productService.save(product);
        assertTrue(result, "O método save deveria retornar true.");
        assertTrue(Files.exists(expectedDestinationPath), "O arquivo de imagem deveria existir com o nome ID_nomeoriginal.extensao.");
        assertEquals(expectedDestinationPath.toString(), product.getImage(), "O caminho da imagem no produto deveria ser atualizado para o destino.");
    }
    @Test
    @DisplayName("Deve salvar imagem JPG corretamente com nome original")
    void deveSalvarImagemJpgCorretamenteComNomeOriginal() {
        Product product = new Product(2, "Test Product JPG", 20.0f, dummySourceImagePathJpg.toString());
        Path expectedDestinationPath = tempStorageDir.resolve("2_source_image_2.jpg");
        boolean result = productService.save(product);
        assertTrue(result, "O método save deveria retornar true para JPG.");
        assertTrue(Files.exists(expectedDestinationPath), "O arquivo JPG deveria existir com o nome ID_nomeoriginal.extensao.");
        assertEquals(expectedDestinationPath.toString(), product.getImage(), "O caminho da imagem JPG no produto deveria ser atualizado para o destino.");
    }
    @Test
    @DisplayName("Deve salvar imagem com espaço no nome original corretamente")
    void deveSalvarImagemComEspacoNomeOriginalCorretamente() {
        Product product = new Product(15, "Test Product Space", 150.0f, dummySourceImagePathWithSpace.toString());
        Path expectedDestinationPath = tempStorageDir.resolve("15_source image with space.png");
        boolean result = productService.save(product);
        assertTrue(result, "O método save deveria retornar true para nome com espaço.");
        assertTrue(Files.exists(expectedDestinationPath), "O arquivo com espaço no nome deveria existir.");
        assertEquals(expectedDestinationPath.toString(), product.getImage(), "O caminho da imagem com espaço no nome deveria ser atualizado.");
    }
    @Test
    @DisplayName("Deve falhar ao salvar imagem de origem inexistente")
    void deveFalharAoSalvarImagemInexistente() {
        Product product = new Product(3, "Non Existent", 30.0f, tempSourceDir.resolve("non_existent.gif").toString());
        Path expectedDestinationPath = tempStorageDir.resolve("3_non_existent.gif");
        boolean result = productService.save(product);
        assertFalse(result, "O método save deveria retornar false para imagem inexistente.");
        assertFalse(Files.exists(expectedDestinationPath), "Nenhum arquivo deveria ser criado no destino para imagem inexistente.");
    }
    @Test
    @DisplayName("Deve falhar ao salvar imagem sem nome original (apenas extensão)")
    void deveFalharAoSalvarImagemSemNomeOriginal() {
        Path sourceOnlyExt = tempSourceDir.resolve(".png");
        try { Files.createFile(sourceOnlyExt); } catch (IOException e) { fail("Falha ao criar arquivo .png"); }
        Product product = new Product(4, "Only Extension", 40.0f, sourceOnlyExt.toString());
        boolean result = productService.save(product);
        assertFalse(result, "O método save deveria retornar false para imagem sem nome original.");
        assertNull(productService.getImagePathById(4), "Nenhum arquivo deveria ser associado ao ID 4.");
    }
    @Test
    @DisplayName("Deve falhar ao salvar com produto nulo")
    void deveFalharAoSalvarComProdutoNulo() {
        Product product = null;
        boolean result = productService.save(product);
        assertFalse(result, "O método save deveria retornar false para produto nulo.");
    }
    @Test
    @DisplayName("Deve falhar ao salvar com caminho da imagem nulo")
    void deveFalharAoSalvarComCaminhoImagemNulo() {
        Product product = new Product(5, "Null Path", 50.0f, null);
        boolean result = productService.save(product);
        assertFalse(result, "O método save deveria retornar false para caminho de imagem nulo.");
    }
    @Test
    @DisplayName("Deve falhar ao salvar com caminho da imagem vazio")
    void deveFalharAoSalvarComCaminhoImagemVazio() {
        Product product = new Product(6, "Empty Path", 60.0f, "");
        boolean result = productService.save(product);
        assertFalse(result, "O método save deveria retornar false para caminho de imagem vazio.");
    }
    @Test
    @DisplayName("Deve remover a imagem corretamente")
    void deveRemoverImagemCorretamente() {
        Product product = new Product(7, "To Remove", 70.0f, dummySourceImagePathPng.toString());
        productService.save(product);
        Path savedImagePath = Paths.get(product.getImage()); 
        assertTrue(Files.exists(savedImagePath), "A imagem deveria existir antes de remover.");
        boolean result = productService.remove(7);
        assertTrue(result, "O método remove deveria retornar true.");
        assertFalse(Files.exists(savedImagePath), "A imagem não deveria mais existir após a remoção.");
    }
    @Test
    @DisplayName("Deve retornar true ao tentar remover imagem inexistente")
    void deveRetornarTrueAoRemoverImagemInexistente() {
        boolean result = productService.remove(99);
        assertTrue(result, "O método remove deveria retornar true mesmo se o arquivo não existir.");
    }
    @Test
    @DisplayName("Deve atualizar a imagem corretamente (substituindo PNG por JPG)")
    void deveAtualizarImagemCorretamente() {
        Product product = new Product(8, "To Update", 80.0f, dummySourceImagePathPng.toString());
        productService.save(product);
        Path initialSavedPath = Paths.get(product.getImage()); 
        assertTrue(Files.exists(initialSavedPath), "Imagem inicial (PNG) deveria existir.");
        product.setImage(dummySourceImagePathJpg.toString()); 
        Path expectedFinalPath = tempStorageDir.resolve("8_source_image_2.jpg"); 
        boolean result = productService.update(product);
        assertTrue(result, "O método update deveria retornar true.");
        assertFalse(Files.exists(initialSavedPath), "Imagem antiga (PNG) não deveria mais existir.");
        assertTrue(Files.exists(expectedFinalPath), "Nova imagem (JPG) deveria existir.");
        assertEquals(expectedFinalPath.toString(), product.getImage(), "O caminho da imagem no produto deveria ser atualizado para o novo destino (JPG).");
    }
    @Test
    @DisplayName("Deve atualizar imagem corretamente mesmo se a imagem anterior não existir")
    void deveAtualizarImagemCorretamenteSemImagemAnterior() {
        Product product = new Product(9, "Update No Previous", 90.0f, dummySourceImagePathPng.toString());
        Path expectedFinalPath = tempStorageDir.resolve("9_source_image_1.png");
        assertFalse(Files.exists(expectedFinalPath), "Imagem inicial não deveria existir.");
        boolean result = productService.update(product);
        assertTrue(result, "O método update deveria retornar true (funcionando como save).");
        assertTrue(Files.exists(expectedFinalPath), "A imagem deveria existir após o update.");
        assertEquals(expectedFinalPath.toString(), product.getImage(), "O caminho da imagem no produto deveria ser atualizado.");
    }
    @Test
    @DisplayName("Deve falhar ao atualizar com imagem de origem inválida")
    void deveFalharAoAtualizarComImagemOrigemInvalida() {
        Product product = new Product(10, "Update Fail", 100.0f, dummySourceImagePathPng.toString());
        productService.save(product);
        Path initialSavedPath = Paths.get(product.getImage());
        assertTrue(Files.exists(initialSavedPath), "Imagem inicial deveria existir.");
        product.setImage(tempSourceDir.resolve("non_existent_update.gif").toString());
        boolean result = productService.update(product);
        assertFalse(result, "O método update deveria retornar false para imagem de origem inválida.");
        assertTrue(Files.exists(initialSavedPath), "Imagem inicial deveria permanecer após falha no update.");
        assertFalse(Files.exists(tempStorageDir.resolve("10_non_existent_update.gif")), "Nenhuma imagem .gif deveria ser criada.");
    }
    @Test
    @DisplayName("Deve obter o caminho da imagem por ID corretamente")
    void deveObterCaminhoImagemPorIdCorretamente() {
        Product product = new Product(11, "Get Path", 110.0f, dummySourceImagePathPng.toString());
        productService.save(product);
        String expectedPath = tempStorageDir.resolve("11_source_image_1.png").toString();
        String actualPath = productService.getImagePathById(11);
        assertNotNull(actualPath, "O caminho retornado não deveria ser nulo.");
        assertEquals(expectedPath, actualPath, "O caminho retornado deveria ser igual ao caminho salvo.");
    }
    @Test
    @DisplayName("Deve retornar nulo ao obter caminho de imagem inexistente por ID")
    void deveRetornarNuloAoObterCaminhoImagemInexistente() {
        int nonExistentId = 123;
        String actualPath = productService.getImagePathById(nonExistentId);
        assertNull(actualPath, "Deveria retornar nulo para um ID de imagem inexistente.");
    }
    @Test
    @DisplayName("Deve atualizar imagem mantendo o mesmo nome original")
    void deveAtualizarImagemMesmoNomeOriginal() {
        Product product = new Product(12, "Update Same Name", 120.0f, dummySourceImagePathPng.toString());
        productService.save(product);
        Path initialSavedPath = Paths.get(product.getImage()); 
        assertTrue(Files.exists(initialSavedPath), "Imagem inicial deveria existir.");
        Path newSourcePath = tempSourceDir.resolve("source_image_1_v2.png");
        try { Files.writeString(newSourcePath, "new content"); } catch (IOException e) { fail("Falha ao criar novo arquivo fonte"); }
        product.setImage(newSourcePath.toString()); 
        Path expectedFinalPath = tempStorageDir.resolve("12_source_image_1_v2.png"); 
        boolean result = productService.update(product);
        assertTrue(result, "O método update deveria retornar true.");
        assertFalse(Files.exists(initialSavedPath), "Imagem antiga não deveria mais existir.");
        assertTrue(Files.exists(expectedFinalPath), "Nova imagem deveria existir.");
        assertEquals(expectedFinalPath.toString(), product.getImage(), "O caminho da imagem no produto deveria ser atualizado.");
    }
}
