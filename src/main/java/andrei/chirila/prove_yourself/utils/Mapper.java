package andrei.chirila.prove_yourself.utils;

import andrei.chirila.prove_yourself.presentation.Presentation;
import andrei.chirila.prove_yourself.presentation.PresentationResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public static List<PresentationResponseDTO> presentationListToResponseDTOList(List<Presentation> presentations, String tempUrl) {
        List<PresentationResponseDTO> responseDTOList = new ArrayList<>();

        for (Presentation presentation : presentations) {
            PresentationResponseDTO responseDTO = new PresentationResponseDTO(
                    presentation.getName(),
                    tempUrl,
                    presentation.getDescription(),
                    presentation.getLink()
            );

            responseDTOList.add(responseDTO);
        }

        return responseDTOList;
    }
}
