package com.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppEntityService
{
    @Autowired
    private AppEntityRepo appEntityRepo;
}
