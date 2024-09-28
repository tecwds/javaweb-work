package entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class UserInfoV2 implements Externalizable {
    private String username;
    private String password;
    private String email;

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(username);
        objectOutput.writeObject(password);
        objectOutput.writeObject(email);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        username = (String) objectInput.readObject();
        password = (String) objectInput.readObject();
        email = (String) objectInput.readObject();
    }
}
