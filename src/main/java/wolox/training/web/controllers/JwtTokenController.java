package wolox.training.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.services.authentication.JwtTokenService;
import wolox.training.web.dtos.JwtTokenIssueRequestDto;

/**
 * A REST controller that wraps a {@link JwtTokenService}.
 */
@RestController
@RequestMapping(value = "/api/tokens", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class JwtTokenController {

    /**
     * The header in which the token should be delivered.
     */
    private static final String TOKEN_HEADER = "X-Token";


    /**
     * The {@link JwtTokenService} being wrapped.
     */
    private final JwtTokenService jwtTokenService;


    /**
     * Constructor.
     *
     * @param jwtTokenService The {@link JwtTokenService} being wrapped.
     */
    @Autowired
    public JwtTokenController(final JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }


    /**
     * Endpoint for issuing a JWT token (i.e this would be a login).
     *
     * @param dto The {@link JwtTokenIssueRequestDto} with the username and password.
     * @return An empty {@link ResponseEntity} with a 201 Created status, together with the Location
     * where the token can be blacklisted sending a DELETE request.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity issueToken(@RequestBody final JwtTokenIssueRequestDto dto) {
        final var token = jwtTokenService.issueToken(dto.getUsername(), dto.getPassword());

        final var uri = ControllerLinkBuilder
            .linkTo(
                ControllerLinkBuilder
                    .methodOn(JwtTokenController.class)
                    .blacklistToken(token.getId())
            ).toUri();

        return ResponseEntity.created(uri)
            .header(TOKEN_HEADER, token.getRawToken())
            .build();
    }

    /**
     * Endpoint for blacklisting the token with the given {@code tokenId}.
     *
     * @param tokenId The id of the token to be deleted.
     * @return A 204 No Content {@link ResponseEntity}.
     */
    @DeleteMapping("/{tokenId:.+}")
    public ResponseEntity blacklistToken(@PathVariable("tokenId") final String tokenId) {
        jwtTokenService.blacklistToken(tokenId);
        return ResponseEntity.noContent().build();
    }
}
