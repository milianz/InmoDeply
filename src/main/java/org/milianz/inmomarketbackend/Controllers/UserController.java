package org.milianz.inmomarketbackend.Controllers;

import lombok.RequiredArgsConstructor;
import org.milianz.inmomarketbackend.Payload.Request.UpdateProfileRequest;
import org.milianz.inmomarketbackend.Payload.Response.MessageResponse;
import org.milianz.inmomarketbackend.Payload.Response.UserProfileResponse;
import org.milianz.inmomarketbackend.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        try {
            UserProfileResponse userProfile = userService.getCurrentUserProfile();
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserProfile(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestParam(value = "removeProfilePicture", defaultValue = "false") Boolean removeProfilePicture) {

        try {
            // Crear el UpdateProfileRequest
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
            updateProfileRequest.setName(name);
            updateProfileRequest.setEmail(email);
            updateProfileRequest.setPhoneNumber(phoneNumber);
            updateProfileRequest.setProfilePicture(profilePicture);
            updateProfileRequest.setRemoveProfilePicture(removeProfilePicture);

            UserProfileResponse updatedProfile = userService.updateUserProfile(updateProfileRequest);
            return ResponseEntity.ok(updatedProfile);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al procesar la imagen"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }
}