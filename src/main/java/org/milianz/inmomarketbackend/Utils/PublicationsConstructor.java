package org.milianz.inmomarketbackend.Utils;

import lombok.Data;
import org.milianz.inmomarketbackend.Domain.Entities.AvailableTime;
import org.milianz.inmomarketbackend.Domain.Entities.PropertyImage;  // Add this line
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.AvailableTimeDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.Publication;

import java.util.List;


@Data
public class PublicationsConstructor {

    public List<PublicationDefaultDTO> PublicationsList(List<Publication> publications) {
        return publications.stream()
                .map(publication -> {
                    PublicationDefaultDTO dto = new PublicationDefaultDTO();
                    dto.setId(publication.getId());
                    dto.setPropertyAddress(publication.getPropertyAddress());
                    dto.setTypeName(publication.getPropertyType().getTypeName());
                    dto.setNeighborhood(publication.getLocation().getNeighborhood());
                    dto.setMunicipality(publication.getLocation().getMunicipality());
                    dto.setDepartment(publication.getLocation().getDepartment());
                    dto.setPropertyTitle(publication.getPropertyTitle());
                    dto.setLongitude(publication.getLongitude());
                    dto.setLatitude(publication.getLatitude());
                    dto.setPropertySize(publication.getPropertySize());
                    dto.setPropertyBedrooms(publication.getPropertyBedrooms());
                    dto.setPropertyFloors(publication.getPropertyFloors());
                    dto.setPropertyParking(publication.getPropertyParking());
                    dto.setPropertyFurnished(publication.getPropertyFurnished());
                    dto.setPropertyDescription(publication.getPropertyDescription());
                    dto.setPropertyPrice(publication.getPropertyPrice());

                    List<String> imageUrls = publication.getPropertyImages()
                            .stream()
                            .map(PropertyImage::getImageUrl)
                            .toList();
                    dto.setPropertyImageUrls(imageUrls);

                    List<AvailableTime> availableTimes = publication.getAvailableTimes();
                    List<AvailableTimeDefaultDTO> availableTimeDTOs = availableTimes.stream()
                            .map(at -> new AvailableTimeDefaultDTO(at.getDayOfWeek(), at.getStartTime(), at.getEndTime()))
                            .toList();
                    dto.setAvailableTimes(availableTimeDTOs);

                    dto.setUserName(publication.getUser().getName());

                    return dto;
                })
                .toList();
    }

    public PublicationDefaultDTO PublicationUnique(Publication publication) {
        PublicationDefaultDTO dto = new PublicationDefaultDTO();
        dto.setId(publication.getId());
        dto.setPropertyAddress(publication.getPropertyAddress());
        dto.setTypeName(publication.getPropertyType().getTypeName());
        dto.setNeighborhood(publication.getLocation().getNeighborhood());
        dto.setMunicipality(publication.getLocation().getMunicipality());
        dto.setDepartment(publication.getLocation().getDepartment());
        dto.setLongitude(publication.getLongitude());
        dto.setLatitude(publication.getLatitude());
        dto.setPropertySize(publication.getPropertySize());
        dto.setPropertyBedrooms(publication.getPropertyBedrooms());
        dto.setPropertyFloors(publication.getPropertyFloors());
        dto.setPropertyParking(publication.getPropertyParking());
        dto.setPropertyFurnished(publication.getPropertyFurnished());
        dto.setPropertyDescription(publication.getPropertyDescription());
        dto.setPropertyPrice(publication.getPropertyPrice());

        List<String> imageUrls = publication.getPropertyImages()
                .stream()
                .map(PropertyImage::getImageUrl)
                .toList();
        dto.setPropertyImageUrls(imageUrls);

        List<AvailableTime> availableTimes = publication.getAvailableTimes();
        List<AvailableTimeDefaultDTO> availableTimeDTOs = availableTimes.stream()
                .map(at -> new AvailableTimeDefaultDTO(at.getDayOfWeek(), at.getStartTime(), at.getEndTime()))
                .toList();
        dto.setAvailableTimes(availableTimeDTOs);

        dto.setUserName(publication.getUser().getName());

        return dto;
    }
}
