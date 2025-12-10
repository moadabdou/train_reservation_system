import React from 'react';
import { ScheduleDTO } from '../types';
import JourneyCard from './JourneyCard';

interface ScheduleListProps {
    schedules: ScheduleDTO[];
    loading: boolean;
    onSelect: (schedule: ScheduleDTO) => void;
}

const ScheduleList: React.FC<ScheduleListProps> = ({ schedules, loading, onSelect }) => {
    if (loading) {
        return <div className="text-center py-8 text-secondary">Loading schedules...</div>;
    }

    if (schedules.length === 0) {
        return <div className="text-center py-8 text-gray-500">No trains found. Try different dates or stations.</div>;
    }

    return (
        <div className="schedule-list">
            {schedules.map(schedule => (
                <JourneyCard key={schedule.id} schedule={schedule} onSelect={onSelect} />
            ))}
        </div>
    );
};

export default ScheduleList;
