package com.spiashko.jpademo.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DomainContextJpaAuditor implements AuditorAware<Long> {

    public Optional<Long> getCurrentAuditor() {
        return Optional.of(31L);
    }
}