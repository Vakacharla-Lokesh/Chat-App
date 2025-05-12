package chatapp.controller;

import chatapp.model.Message;
import chatapp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final MessageService messageService;

    @Autowired
    public ConversationController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<Message> sendMessage(
            @PathVariable String conversationId,
            @RequestBody Message message) {
        Message saved = messageService.saveMessage(conversationId, message);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable String conversationId) {
        List<Message> messages = messageService.getMessages(conversationId);
        return ResponseEntity.ok(messages);
    }
}
