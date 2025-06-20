package org.milianz.inmomarketbackend.Services;

import lombok.RequiredArgsConstructor;
import org.milianz.inmomarketbackend.Domain.Entities.User;
import org.milianz.inmomarketbackend.Domain.Repositories.iUserRepository;
import org.milianz.inmomarketbackend.Payload.Request.UpdateProfileRequest;
import org.milianz.inmomarketbackend.Payload.Response.UserProfileResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final iUserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public UserProfileResponse getCurrentUserProfile() {
        User currentUser = getCurrentUser();
        return mapToUserProfileResponse(currentUser);
    }

    @Transactional
    public UserProfileResponse updateUserProfile(UpdateProfileRequest updateProfileRequest) throws IOException {
        User currentUser = getCurrentUser();

        // Verificar si el email ya está en uso por otro usuario
        if (!currentUser.getEmail().equals(updateProfileRequest.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(updateProfileRequest.getEmail());
            if (existingUser.isPresent()) {
                throw new RuntimeException("El email ya está en uso por otro usuario");
            }
        }


        currentUser.setName(updateProfileRequest.getName());
        currentUser.setEmail(updateProfileRequest.getEmail());
        currentUser.setPhoneNumber(updateProfileRequest.getPhoneNumber());


        if (updateProfileRequest.getRemoveProfilePicture() != null && updateProfileRequest.getRemoveProfilePicture()) {
            // Eliminar foto existente
            if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) {
                cloudinaryService.deleteImage(currentUser.getProfilePicture());
                currentUser.setProfilePicture(null);
            }
        } else if (updateProfileRequest.getProfilePicture() != null && !updateProfileRequest.getProfilePicture().isEmpty()) {
            // Subir nueva foto
            // Eliminar foto anterior si existe
            if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) {
                cloudinaryService.deleteImage(currentUser.getProfilePicture());
            }

            // Validar archivo
            validateProfilePicture(updateProfileRequest.getProfilePicture());

            // Subir nueva imagen
            String imageUrl = cloudinaryService.uploadImage(updateProfileRequest.getProfilePicture(), "profile_pictures");
            currentUser.setProfilePicture(imageUrl);
        }

        User updatedUser = userRepository.save(currentUser);
        return mapToUserProfileResponse(updatedUser);
    }

    private void validateProfilePicture(MultipartFile file) {
        // Validar que el archivo no esté vacío
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo no puede estar vacío");
        }

        // Validar que sea una imagen
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("El archivo debe ser una imagen");
        }

        // Validar tamaño (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("El archivo no puede ser mayor a 5MB");
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}