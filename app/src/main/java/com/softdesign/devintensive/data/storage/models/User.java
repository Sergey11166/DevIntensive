package com.softdesign.devintensive.data.storage.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;

/**
 * @author Sergey Vorobyev.
 */
@Entity(active = true, nameInDb = "USERS")
public class User {

    @Id
    private long id;

    @NotNull
    @Unique
    private String remoteId;

    private String photo;

    @NotNull
    @Unique
    private String fullName;

    @NotNull
    @Unique
    private String searchName;

    private int rating;

    private int codeLines;

    private int projects;

    private String bio;

    @ToMany(joinProperties = @JoinProperty(name = "remoteId", referencedName = "userRemoteId"))
    private List<Repository> repositories;
}
