'use client'

import Image from 'next/image'
import trashIcon from "../public/images/trash.svg"
import editIcon from "../public/images/edit.svg"
import eyeIcon from "../public/images/eye-search.svg"

export interface Patient {
    id?: string
    firstName: string
    lastName: string
    birthDate: string
    address?: string
    phone?: string
    gender: 'MALE' | 'FEMALE'
    risque?: string
}

interface PatientTableProps {
    patients: Patient[]
    loading: boolean
    onView: (id: string) => void
    onUpdate: (patient: Patient) => void
    onDelete: (id: string) => void
    onAddClick: () => void
}

export default function PatientTable({ patients, loading, onView, onUpdate, onDelete, onAddClick }: PatientTableProps) {
    return (
        <main className="min-h-screen bg-gray-50 p-10">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold text-gray-800">Base de donnée des patients</h1>
                <button
                    onClick={onAddClick}
                    className="px-4 py-2 bg-blue-600 text-white rounded cursor-pointer hover:bg-blue-700"
                >
                    Ajouter un patient
                </button>
            </div>

            {loading ? (
                <p>Chargement des patients...</p>
            ) : (
                <div className="bg-white rounded-lg shadow-md overflow-hidden">
                    <table className="min-w-full">
                        <thead className="bg-gray-100 text-left text-sm text-gray-600">
                        <tr>
                            <th className="w-1/5 px-6 py-3">Nom</th>
                            <th className="w-1/5 px-6 py-3">Prénom</th>
                            <th className="w-1/5 px-6 py-3">Âge</th>
                            <th className="w-1/5 px-6 py-3">Risque</th>
                            <th className="w-1/5 px-6 py-3">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {patients.length > 0 ? (
                            patients.map((patient, index) => {
                                const birthDate = new Date(patient.birthDate)
                                const age = new Date().getFullYear() - birthDate.getFullYear()
                                return (
                                    <tr key={index} className="border-t border-gray-200 hover:bg-gray-50">
                                        <td className="px-6 py-4">{patient.lastName}</td>
                                        <td className="px-6 py-4">{patient.firstName}</td>
                                        <td className="px-6 py-4">{age} ans</td>
                                        <td className="px-6 py-4">
                                            <span className={`font-semibold ${
                                                patient.risque === 'In Danger' ? 'text-red-600' :
                                                    patient.risque === 'EarlyOnset' ? 'text-orange-500' :
                                                        patient.risque === 'Borderline' ? 'text-blue-500' :
                                                            'text-gray-500'
                                            }`}>
                                              {patient.risque || '—'}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <button
                                                onClick={() => onView(patient.id as string)}
                                                className="px-1.5 py-1.5 mr-2 bg-blue-500 text-white rounded cursor-pointer hover:bg-blue-600"
                                            >
                                                <Image src={eyeIcon} alt="Modifier" width={19} height={19} />
                                            </button>
                                            <button
                                                onClick={() => onUpdate(patient)}
                                                className="px-1.5 py-1.5 mr-2 bg-orange-500 text-white rounded cursor-pointer hover:bg-orange-600"
                                            >
                                                <Image src={editIcon} alt="Modifier" width={19} height={19} />
                                            </button>
                                            <button
                                                onClick={() => onDelete(patient.id as string)}
                                                className="px-1.5 py-1.5 bg-red-500 text-white rounded cursor-pointer hover:bg-red-600"
                                            >
                                                <Image src={trashIcon} alt="Supprimer" width={19} height={19} />
                                            </button>
                                        </td>
                                    </tr>
                                )
                            })
                        ) : (
                            <tr>
                                <td colSpan={5} className="text-center py-4 text-gray-500">
                                    Aucun patient trouvé.
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            )}
        </main>
    )
}
