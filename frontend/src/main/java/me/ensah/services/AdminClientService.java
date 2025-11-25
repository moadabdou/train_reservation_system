package me.ensah.services;

import com.fasterxml.jackson.core.type.TypeReference;
import me.ensah.net.ApiClient;
import java.util.Map;

/**
 * AdminClientService - Handles all admin-related API calls for client
 * management
 * 
 * Provides methods to:
 * - Fetch all clients with pagination
 * - Search clients by name/email
 * - Get client details
 * - Get dashboard statistics
 */
public class AdminClientService {

    private final ApiClient api;

    public AdminClientService(ApiClient api) {
        this.api = api;
    }

    /**
     * Get all clients with pagination
     * 
     * @param page page number (0-indexed)
     * @param size number of records per page
     * @return Map containing clients list and pagination info
     */
    public Map<String, Object> getAllClients(int page, int size) throws Exception {
        String path = "/admin/clients?page=" + page + "&size=" + size;
        return api.get(path, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * Search clients by name or email
     * 
     * @param searchTerm search keyword
     * @param page       page number
     * @param size       page size
     * @return Map containing filtered clients and pagination info
     */
    public Map<String, Object> searchClients(String searchTerm, int page, int size) throws Exception {
        String path = "/admin/clients?search=" + searchTerm + "&page=" + page + "&size=" + size;
        return api.get(path, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * Get dashboard statistics
     * 
     * @return Map containing total users, bookings, trains, stations, and revenue
     */
    public Map<String, Object> getDashboardStats() throws Exception {
        String path = "/admin/dashboard-stats";
        return api.get(path, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * Get details for a specific client
     * 
     * @param clientId the client's ID
     * @return Map containing client details and booking statistics
     */
    public Map<String, Object> getClientDetails(Long clientId) throws Exception {
        String path = "/admin/clients/" + clientId;
        return api.get(path, new TypeReference<Map<String, Object>>() {
        });
    }
}
