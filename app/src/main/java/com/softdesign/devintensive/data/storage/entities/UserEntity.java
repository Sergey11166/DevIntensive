package com.softdesign.devintensive.data.storage.entities;

import com.softdesign.devintensive.data.network.restmodels.Repo;
import com.softdesign.devintensive.data.network.restmodels.User;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * @author Sergey Vorobyev.
 */
@Entity(active = true, nameInDb = "USERS")
public class UserEntity {

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

    private int countCodeLines;

    private int countProjects;

    private String bio;

    @ToMany(joinProperties = {@JoinProperty(name = "remoteId", referencedName = "userRemoteId")})
    private List<RepositoryEntity> repositories;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1814575071)
    private transient UserEntityDao myDao;

    public UserEntity(User user) {
        remoteId = user.getId();
        photo = user.getPublicInfo().getPhoto();
        fullName = user.getFirstName() + " " + user.getSecondName();
        searchName = fullName.toUpperCase();
        rating = user.getProfileValues().getRating();
        countCodeLines = user.getProfileValues().getLinesCode();
        countProjects = user.getProfileValues().getProjects();
        bio = user.getPublicInfo().getBio();
        repositories = new ArrayList<>(user.getRepositories().getRepo().size()) ;
        for (Repo r: user.getRepositories().getRepo()) repositories.add(new RepositoryEntity(r, remoteId));
    }

    @Generated(hash = 726522695)
    public UserEntity(long id, @NotNull String remoteId, String photo, @NotNull String fullName,
            @NotNull String searchName, int rating, int countCodeLines, int countProjects, String bio) {
        this.id = id;
        this.remoteId = remoteId;
        this.photo = photo;
        this.fullName = fullName;
        this.searchName = searchName;
        this.rating = rating;
        this.countCodeLines = countCodeLines;
        this.countProjects = countProjects;
        this.bio = bio;
    }

    @Generated(hash = 1433178141)
    public UserEntity() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRemoteId() {
        return this.remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSearchName() {
        return this.searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public int getRating() {
        return this.rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getCountCodeLines() {
        return this.countCodeLines;
    }

    public void setCountCodeLines(int countCodeLines) {
        this.countCodeLines = countCodeLines;
    }

    public int getCountProjects() {
        return this.countProjects;
    }

    public void setCountProjects(int countProjects) {
        this.countProjects = countProjects;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1664108813)
    public List<RepositoryEntity> getRepositories() {
        if (repositories == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RepositoryEntityDao targetDao = daoSession.getRepositoryEntityDao();
            List<RepositoryEntity> repositoriesNew = targetDao._queryUserEntity_Repositories(remoteId);
            synchronized (this) {
                if (repositories == null) {
                    repositories = repositoriesNew;
                }
            }
        }
        return repositories;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 438307964)
    public synchronized void resetRepositories() {
        repositories = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 287999134)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserEntityDao() : null;
    }
}
