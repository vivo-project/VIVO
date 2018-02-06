/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CollaborationData;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaboration;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaborator;

import java.util.ArrayList;
import java.util.List;

public class CollaborationDataViewHelper {
    private static final int MAX_COLLABORATORS = 35;
    private CollaborationData data;

    private List<Collaborator> collaborators = null;

    private int[][] collaborationMatrix = null;

    public CollaborationDataViewHelper(CollaborationData data) {
        this.data = data;
    }

    public int getCollaboratorsCount() {
        init(MAX_COLLABORATORS);
        return data.getCollaborators().size();
    }

    public int[][] getCollaborationMatrix() {
        init(MAX_COLLABORATORS);
        return collaborationMatrix;
    }

    public List<Collaborator> getCollaborators() {
        init(MAX_COLLABORATORS);
        return collaborators;
    }

    private synchronized void init(int max) {
        if (collaborators != null) {
            return;
        }

        collaborators = new ArrayList<>(max);

        // Find the top N collaborators

        // Threshold is the lowest number of activities currently in the table
        // Start at maximum possible value
        int threshold = Integer.MAX_VALUE;

        // Iterate through each collaborator
        for (Collaborator collaborator : data.getCollaborators()) {

            // Only include the collaborator if it isn't the focus of the view
            if (collaborator.getCollaboratorID() != data.getEgoCollaborator().getCollaboratorID()) {

                // If the number of activities exceeds the threshold, it needs to be included in the top N
                if (collaborator.getNumOfActivities() > threshold) {
                    // If we've filled the Top N
                    if (collaborators.size() == max - 1) {
                        // Remove the last (lowest) entry of the Top N
                        collaborators.remove(collaborators.size() - 1);
                    }

                    // Find the place to insert the new entry
                    int insert = collaborators.size();
                    while (insert > 0) {
                        insert--;
                        if (collaborators.get(insert).getNumOfActivities() > collaborator.getNumOfActivities()) {
                            insert++;
                            break;
                        }
                    }

                    if (insert < 0) {
                        // If we didn't find a place to insert, insert it at the start
                        collaborators.add(0, collaborator);
                    } else if (insert < collaborators.size()) {
                        // If the insert position is before the end of the list, insert at that position
                        collaborators.add(insert, collaborator);
                    } else {
                        // Otherwise, add to the end of the list
                        collaborators.add(collaborator);
                    }

                    // Update the threshold with the new lowest position entry
                    threshold = collaborators.get(collaborators.size() - 1).getNumOfActivities();
                } else {
                    // If we are below the threshold, check if the top N is full
                    if (collaborators.size() < max - 1) {
                        // Top N is incomplete, so add to the end of the list
                        collaborators.add(collaborator);

                        // And record the new collaboration as the threshold
                        threshold = collaborator.getNumOfActivities();
                    }
                }
            }
        }

        // Now add the person that is the focus to the start of the list
        collaborators.add(0, data.getEgoCollaborator());

        // If we only want to visualize collaborations between the main focus and others, set this to false
        boolean fullMatrix = true;

        // Initialise the matrix
        collaborationMatrix = new int[collaborators.size()][collaborators.size()];

        // For every row in the matrix
        for (int x = 0; x < collaborators.size(); x++) {
            // Get the collaborator associated with this row
            Collaborator collaboratorX = collaborators.get(x);

            // Generate a list of possible collaborations for this row
            List<Collaboration> possibleCollaborations = new ArrayList<>();

            // Go through all of the collaborations
            for (Collaboration collaboration : data.getCollaborations()) {
                // Get the collaborators
                Collaborator source = collaboration.getSourceCollaborator();
                Collaborator target = collaboration.getTargetCollaborator();

                // If the collaborator for this row is involved in the collaboration, add it to the list of possibles
                if (source.getCollaboratorID() == collaboratorX.getCollaboratorID() || target.getCollaboratorID() == collaboratorX.getCollaboratorID()) {
                    possibleCollaborations.add(collaboration);
                }
            }

            // For every column in the matrix
            for (int y = 0; y < collaborators.size(); y++) {
                // Get the collaborator associated with this column
                Collaborator collaboratorY = collaborators.get(y);

                // If this is the first row, first column, or we are creating a full matrix of all collaborations
                if (x == 0 || y == 0 || fullMatrix) {
                    // Go through all of the possible collaborations
                    for (Collaboration collaboration : possibleCollaborations) {
                        // Get the collaborators
                        Collaborator source = collaboration.getSourceCollaborator();
                        Collaborator target = collaboration.getTargetCollaborator();

                        // If the source is the row collaborator and the target is the column collaborator
                        if (source.getCollaboratorID() == collaboratorX.getCollaboratorID() && target.getCollaboratorID() == collaboratorY.getCollaboratorID()) {
                            // Add the number of collaborations to the matrix, and stop processing collaborations
                            collaborationMatrix[x][y] = collaboration.getNumOfCollaborations();
                            break;
                        }

                        // If the source is the column collaborator and the target is the row collaborator
                        if (source.getCollaboratorID() == collaboratorY.getCollaboratorID() && target.getCollaboratorID() == collaboratorX.getCollaboratorID()) {
                            // Add the number of collaborations to the matrix, and stop processing collaborations
                            collaborationMatrix[x][y] = collaboration.getNumOfCollaborations();
                            break;
                        }
                    }
                }
            }
        }

        // Reset the activity count for the top left matrix entry (focus - focus collaboration)
        collaborationMatrix[0][0] = 0;
    }
}
