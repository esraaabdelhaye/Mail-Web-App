package com.mailapp.mailbackend.service.User.auth;

import com.mailapp.mailbackend.enums.ChainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class ChainFactory {

    @Autowired
    @Qualifier("uniqueEmail")
    private IHandler uniqueEmail;

    @Autowired
    @Qualifier("emailExists")
    private IHandler emailExists;

    @Autowired
    @Qualifier("correctPassword")
    private IHandler correctPassword;

    @Autowired
    @Qualifier("longEnough")
    private IHandler longEnough;

    private final ParentHandler chainEnd = new ParentHandler(null);


    @PostConstruct
    public void setupChains() {
        uniqueEmail.setNext(null);
        emailExists.setNext(null);
        correctPassword.setNext(null);
        longEnough.setNext(null);
    }


    public IHandler getChain(ChainType chain){

        IHandler chainHead = new ParentHandler(null);

        switch (chain) {
            case Register:
                // Sign Up Chain: uniqueEmail (start) -> longEnough -> (chainEnd)
                uniqueEmail.setNext(longEnough);
                longEnough.setNext(chainEnd);
                chainHead.setNext(uniqueEmail);
                break;

            case Login:
                // Log In Chain: emailExists (start) -> correctPassword -> (chainEnd)
                emailExists.setNext(correctPassword);
                correctPassword.setNext(chainEnd);
                chainHead.setNext(emailExists);
                break;

            default:
                throw new IllegalArgumentException("Unknown chain type: " + chain);
        };

        return chainHead;
    }
}