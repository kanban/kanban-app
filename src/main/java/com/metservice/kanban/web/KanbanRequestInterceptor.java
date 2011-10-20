package com.metservice.kanban.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.metservice.kanban.KanbanService;

public class KanbanRequestInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private KanbanService kanbanService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        request.setAttribute("service", kanbanService);

        return true;
    }
}
