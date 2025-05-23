package com.example.services;
import com.example.entities.Product;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
public class ProductService {
    private final String imageStoragePath;
    public ProductService(String imageStoragePath) {
        if (imageStoragePath != null && !imageStoragePath.endsWith(File.separator)) {
            this.imageStoragePath = imageStoragePath + File.separator;
        } else {
            this.imageStoragePath = imageStoragePath;
        }
        try {
            Files.createDirectories(Paths.get(this.imageStoragePath));
        } catch (IOException e) {
            throw new RuntimeException("Falha ao criar o diretório de armazenamento de imagens: " + this.imageStoragePath, e);
        }
    }
    private String getOriginalFilename(Path path) {
         if (path == null || path.getFileName() == null) {
            return "";
        }
        return path.getFileName().toString();
    }
    public boolean save(Product product) {
        if (product == null || product.getImage() == null || product.getImage().trim().isEmpty()) {
            return false;
        }
        Path sourcePath = Paths.get(product.getImage());
        String originalFilename = getOriginalFilename(sourcePath);
        if (originalFilename.isEmpty() || originalFilename.startsWith(".")) { 
             return false;
        }
        Path destinationPath = Paths.get(String.format("%s%d_%s", this.imageStoragePath, product.getId(), originalFilename));
        if (Files.exists(sourcePath)) {
            try {
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(destinationPath.toString()); 
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
             return false;
        }
    }
    public String getImagePathById(int id) {
        File directory = new File(this.imageStoragePath);
        if (!directory.exists() || !directory.isDirectory()) {
            return null;
        }
        final String prefix = String.valueOf(id) + "_";
        File[] matches = directory.listFiles((dir, name) -> name.startsWith(prefix));
        if (matches != null && matches.length > 0) {
             return matches[0].getAbsolutePath();
        } else {
            return null; 
        }
    }
    public boolean update(Product product) {
        if (product == null) {
             return false;
        }
        String oldImagePath = getImagePathById(product.getId());
        boolean savedSuccessfully = save(product);
        if (savedSuccessfully && oldImagePath != null) {
            if (!oldImagePath.equals(product.getImage())) {
                 try {
                    Files.deleteIfExists(Paths.get(oldImagePath));
                } catch (IOException e) {
                    System.err.println("Erro ao remover imagem antiga " + oldImagePath + " durante a atualização: " + e.getMessage());
                }
            }
        }
        return savedSuccessfully; 
    }
    public boolean remove(int id) {
        String imagePathStr = getImagePathById(id);
        if (imagePathStr != null) {
            Path path = Paths.get(imagePathStr);
            try {
                return Files.deleteIfExists(path);
            } catch (IOException e) {
                return false;
            }
        } else {
             return true; 
        }
    }
}
