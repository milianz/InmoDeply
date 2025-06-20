package org.milianz.inmomarketbackend.Services;

import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationSaveDTO;
import org.milianz.inmomarketbackend.Domain.Entities.PropertyType;
import org.milianz.inmomarketbackend.Domain.Repositories.iPropertyTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PropertyTypeService {

    @Autowired
    private iPropertyTypeRepository propertyTypeRepository;

    public PropertyType createPropertyType(PublicationSaveDTO publicationSaveDTO) {

        return propertyTypeRepository.findByTypeName(publicationSaveDTO.getTypeName()).orElseGet(() -> {;
            PropertyType newPropertyType = new PropertyType();
            newPropertyType.setTypeName(publicationSaveDTO.getTypeName());
            return propertyTypeRepository.save(newPropertyType);
        });
    }
}
