package com.se_frms.blacklistEntry.service;

import com.se_frms.blacklistEntry.dto.BlacklistEntryRequestDTO;
import com.se_frms.blacklistEntry.dto.BlacklistEntryResponseDTO;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface BlacklistEntryService {

    BlacklistEntryResponseDTO addEntry(BlacklistEntryRequestDTO request);

    BlacklistEntryResponseDTO removeEntry(Integer id);

    Page<BlacklistEntryResponseDTO> getAllEntries(int page, int size, Map<String, String> filters);

    BlacklistEntryResponseDTO getEntryById(Integer id);
}
