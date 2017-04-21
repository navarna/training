package com.navarna.computerdb.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.navarna.computerdb.model.Company;
import com.navarna.computerdb.model.Company.CompanyBuilder;
import com.navarna.computerdb.model.Computer;
import com.navarna.computerdb.model.Computer.ComputerBuilder;
import com.navarna.computerdb.model.Page;
import com.navarna.computerdb.service.ServiceCompanyImpl;
import com.navarna.computerdb.service.ServiceComputerImpl;
import com.navarna.computerdb.validator.ValidationEntrer;

/**
 * Servlet implementation class AddComputer
 */
public class AddComputer extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ServiceComputerImpl servComputer = new ServiceComputerImpl();
    private ServiceCompanyImpl servCompany = new ServiceCompanyImpl();
    private Integer reponse = null;

    protected ArrayList<Company> initialisationListeCompany() {
        ArrayList<Company> informationCompany = new ArrayList<Company>();
        boolean fini = false;
        servCompany.choisirNbElement(100);
        servCompany.resetPage();
        int compteur = 0;
        while (!fini) {
            Page<Company> page = servCompany.liste();
            compteur++;
            servCompany.choisirPage(compteur);
            if (page == null) {
                fini = true;
            } else {
                for (Company company : page.getPage()) {
                    informationCompany.add(company);
                }
            }
        }
        return informationCompany;
    }

    public void demandeInsert(String name, String introduced, String discontinued, String idCompany) {
        int id;
        if ((id = ValidationEntrer.EntrerValide(name, introduced, discontinued, idCompany)) != -1) {
            LocalDate pIntroduced = LocalDate.parse(introduced);
            LocalDate pDiscontinued = LocalDate.parse(discontinued);
            CompanyBuilder companyBuilder = new CompanyBuilder("null").setId(new Long(id));
            Company company = companyBuilder.build();
            ComputerBuilder computerBuilder = new ComputerBuilder(name).setIntroduced(pIntroduced)
                    .setDiscontinued(pDiscontinued);
            computerBuilder.setCompany(company);
            computerBuilder.setId(0L);
            Computer computer = computerBuilder.build();
            reponse = servComputer.insert(computer);
        }
    }

    public void lireParametre(HttpServletRequest request) {
        String name = request.getParameter("name");
        String introduced = request.getParameter("introduced");
        String discontinued = request.getParameter("discontinued");
        String idCompany = request.getParameter("idCompany");
        demandeInsert(name, introduced, discontinued, idCompany);
    }

    public void ecrireAttribute(HttpServletRequest request) {
        ArrayList<Company> informationCompany = initialisationListeCompany();
        request.setAttribute("listeCompany", informationCompany);
        if(reponse!=null) {
            request.setAttribute("reponse", reponse);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher fichierJSP = this.getServletContext()
                .getRequestDispatcher("/resources/views/addComputer.jsp");
        ecrireAttribute(request);
        fichierJSP.forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        System.out.println("dans post");
        lireParametre(request);
        doGet(request, response);
    }

}