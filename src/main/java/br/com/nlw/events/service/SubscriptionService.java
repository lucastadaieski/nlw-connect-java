package br.com.nlw.events.service;

import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.UserIndicadorNotFoundException;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repository.EventRepo;
import br.com.nlw.events.repository.SubscriptionRepo;
import br.com.nlw.events.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    @Autowired
    private EventRepo evtRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubscriptionRepo subRepo;

    public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId){
        //recuperar o evento pelo nome
        Event evt = evtRepo.findByPrettyName(eventName);
        //caso alternativo 2
        if (evt == null) {
            throw new EventNotFoundException("Evento: "+ eventName +" não existe");
        }
        //caso alternativo 1
        User userRec = userRepo.findByEmail(user.getEmail());
        if (userRec == null){
            userRec = userRepo.save(user);
        }

        User indicador = userRepo.findById(userId).orElse(null);
        if (indicador == null){
            throw new UserIndicadorNotFoundException("Usuario " + userId + " indicador não existe");
        }

        Subscription subs = new Subscription();
        subs.setEvent(evt);
        subs.setSubscriber(userRec);
        subs.setIndication(indicador);

        //caso alternativo 3
        Subscription tmpSubs = subRepo.findByEventAndSubscriber(evt,userRec);
        if (tmpSubs != null){
            throw new SubscriptionConflictException("Já existe inscrição para o usuário " + userRec.getName() + " no evento " + evt.getTitle());
        }

        Subscription res = subRepo.save(subs);

        return new SubscriptionResponse(res.getSubscriptionNumber(), "http://codecraft.com/subscription/"+res.getEvent().getPrettyName()+"/"+res.getSubscriber().getId());
    }
}
