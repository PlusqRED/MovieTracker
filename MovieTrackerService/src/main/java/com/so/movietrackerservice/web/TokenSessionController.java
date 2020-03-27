package com.so.movietrackerservice.web;

import com.so.movietrackerservice.domain.db.BotUser;
import com.so.movietrackerservice.repository.BotUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Optional;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenSessionController {

    private final BotUserRepository botUserRepository;

    @GetMapping
    public ResponseEntity<String> valid(@PathParam("token") String token) {
        return botUserRepository.findByChromeExtensionToken(token).isPresent()
                ? ResponseEntity.status(HttpStatus.CREATED).body("Token is valid")
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@PathParam("token") String token) {
        Optional<BotUser> botUserOptional = botUserRepository.findByChromeExtensionToken(token);
        if (botUserOptional.isPresent()) {
            BotUser botUser = botUserOptional.get();
            botUser.setChromeExtensionToken(null);
            botUserRepository.save(botUser);
            return ResponseEntity.ok("Token has been deleted");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid");
        }
    }
}
