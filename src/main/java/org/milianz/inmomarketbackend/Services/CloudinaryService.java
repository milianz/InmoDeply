package org.milianz.inmomarketbackend.Services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.milianz.inmomarketbackend.Domain.Entities.PropertyImage;
import org.milianz.inmomarketbackend.Domain.Entities.Publication;
import org.milianz.inmomarketbackend.Domain.Repositories.iPropertyImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private iPropertyImageRepository propertyImageRepository;

    public List<PropertyImage> uploadImage(MultipartFile[] files, Publication publication) throws IOException {
        List<PropertyImage> propertyImages = new ArrayList<>();
        for (MultipartFile file : files) {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            String imageUrl = uploadResult.get("url").toString();

            PropertyImage propertyImage = new PropertyImage();
            propertyImage.setImageUrl(imageUrl);
            propertyImage.setPublication(publication);
            propertyImage.setIsMain(false);
            propertyImage = propertyImageRepository.save(propertyImage);
        }

        return propertyImages;
    }

    // Método sobrecargado para subir imágenes de perfil
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image"
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return uploadResult.get("url").toString();
    }

    // Método para eliminar imágenes
    public void deleteImage(String imageUrl) throws IOException {
        try {
            // Extraer el public_id de la URL de Cloudinary
            String publicId = extractPublicIdFromUrl(imageUrl);

            if (publicId != null && !publicId.isEmpty()) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            // Log del error pero no lanzar excepción para no afectar el flujo principal
            System.err.println("Error al eliminar imagen de Cloudinary: " + e.getMessage());
        }
    }

    // Método auxiliar para extraer el public_id de la URL de Cloudinary
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            // Ejemplo de URL: https://res.cloudinary.com/cloud_name/image/upload/v1234567890/folder/image_name.jpg
            // El public_id sería: folder/image_name

            if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
                return null;
            }

            // Buscar la parte después de "/upload/"
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                return null;
            }

            // Tomar la segunda parte y remover la versión si existe
            String pathWithVersion = parts[1];

            // Si hay versión (v1234567890), la removemos
            if (pathWithVersion.startsWith("v") && pathWithVersion.contains("/")) {
                String[] versionParts = pathWithVersion.split("/", 2);
                if (versionParts.length > 1) {
                    pathWithVersion = versionParts[1];
                }
            }

            // Remover la extensión del archivo
            int lastDotIndex = pathWithVersion.lastIndexOf('.');
            if (lastDotIndex > 0) {
                pathWithVersion = pathWithVersion.substring(0, lastDotIndex);
            }

            return pathWithVersion;

        } catch (Exception e) {
            System.err.println("Error al extraer public_id de la URL: " + e.getMessage());
            return null;
        }
    }
}